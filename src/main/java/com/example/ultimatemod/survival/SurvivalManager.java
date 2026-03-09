package com.example.ultimatemod.survival;

import com.example.ultimatemod.ModPackets;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SurvivalManager {

    // Per-player survival state (server-side only)
    private static final Map<UUID, SurvivalData> SURVIVAL_MAP = new HashMap<>();

    // Tick intervals
    private static final int THIRST_DRAIN_INTERVAL  = 1200; // 1 thirst per 60 s
    private static final int FATIGUE_BUILD_INTERVAL = 1800; // 1 fatigue per 90 s
    private static final int DAMAGE_INTERVAL        = 600;  // environment damage every 30 s
    private static final int SYNC_INTERVAL          = 40;   // sync to client every 2 s
    private static int syncTick = 0;

    public static void initialize() {
        // Load data when player joins
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            SURVIVAL_MAP.put(handler.player.getUuid(), new SurvivalData());
        });

        // Remove data when player leaves
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            SURVIVAL_MAP.remove(handler.player.getUuid());
        });

        // Main tick update
        ServerTickEvents.END_SERVER_TICK.register(SurvivalManager::onServerTick);

        // Drink water when right-clicking a water block
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

            BlockPos pos = hitResult.getBlockPos();
            // Accept any water block — still or flowing — so the player can always
            // drink when next to or clicking into a body of water.
            boolean isWater = world.getFluidState(pos).isOf(Fluids.WATER);

            if (!isWater) return ActionResult.PASS;

            SurvivalData data = SURVIVAL_MAP.get(serverPlayer.getUuid());
            if (data == null) return ActionResult.PASS;

            if (data.thirst < 20) {
                data.thirst = 20;
                serverPlayer.sendMessage(Text.literal("§bYou drank some water. Thirst restored!"), true);
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_DRINK,
                        SoundCategory.PLAYERS, 0.5f, 1.0f);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        // Reset fatigue when player sleeps
        EntitySleepEvents.STOP_SLEEPING.register((entity, sleepingPos) -> {
            if (entity instanceof ServerPlayerEntity serverPlayer) {
                SurvivalData data = SURVIVAL_MAP.get(serverPlayer.getUuid());
                if (data != null) {
                    data.fatigue = 0;
                    data.wasSleeping = false;
                    serverPlayer.sendMessage(Text.literal("§aYou feel well rested!"), true);
                }
            }
        });
    }

    // -----------------------------------------------------------------------
    // Main server tick
    // -----------------------------------------------------------------------

    private static void onServerTick(MinecraftServer server) {
        syncTick++;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            SurvivalData data = SURVIVAL_MAP.computeIfAbsent(player.getUuid(), k -> new SurvivalData());

            updateThirst(player, data);
            updateFatigue(player, data);
            updateTemperature(player, data);
            applyEffects(player, data);
        }

        // Sync stats to all clients periodically
        if (syncTick >= SYNC_INTERVAL) {
            syncTick = 0;
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                SurvivalData data = SURVIVAL_MAP.get(player.getUuid());
                if (data != null) sendSyncPacket(player, data);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Stat updates
    // -----------------------------------------------------------------------

    private static void updateThirst(ServerPlayerEntity player, SurvivalData data) {
        data.thirstTick++;
        if (data.thirstTick >= THIRST_DRAIN_INTERVAL) {
            data.thirstTick = 0;
            if (data.thirst > 0) {
                data.thirst--;
                if (data.thirst <= 5) {
                    player.sendMessage(Text.literal("§9You are getting thirsty!"), true);
                }
            }
        }
    }

    private static void updateFatigue(ServerPlayerEntity player, SurvivalData data) {
        data.fatigueTick++;
        if (data.fatigueTick >= FATIGUE_BUILD_INTERVAL) {
            data.fatigueTick = 0;
            if (data.fatigue < 100) {
                data.fatigue++;
                if (data.fatigue == 60) {
                    player.sendMessage(Text.literal("§eYou are feeling tired..."), true);
                }
                if (data.fatigue == 85) {
                    player.sendMessage(Text.literal("§cYou are exhausted! Sleep soon!"), true);
                }
            }
        }
    }

    private static void updateTemperature(ServerPlayerEntity player, SurvivalData data) {
        // Read biome temperature from the player's current position
        Biome biome = player.getWorld()
                .getBiome(player.getBlockPos()).value();
        float biomeTemp = biome.getTemperature();
        data.temperature = biomeTemp;
    }

    // -----------------------------------------------------------------------
    // Status effects based on survival stats
    // -----------------------------------------------------------------------

    private static void applyEffects(ServerPlayerEntity player, SurvivalData data) {
        data.damageTick++;

        // --- Thirst effects ---
        if (data.thirst <= 0) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 0, false, false));
        } else if (data.thirst <= 5) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0, false, false));
        }

        // --- Fatigue effects ---
        if (data.fatigue >= 80) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 0, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 0, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0, false, false));
        } else if (data.fatigue >= 60) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0, false, false));
        }

        // --- Temperature effects (every 30 s) ---
        if (data.damageTick >= DAMAGE_INTERVAL) {
            data.damageTick = 0;

            float temp = data.temperature;

            if (temp < 0.15f) {
                // Freezing biome (tundra, ice plains, etc.)
                if (player.getWorld().isSkyVisible(player.getBlockPos())) {
                    player.damage(player.getDamageSources().freeze(), 1.5f);
                    player.sendMessage(Text.literal("§bYou are freezing!"), true);
                }
            } else if (temp > 1.5f) {
                // Scorching biome (desert, badlands)
                if (player.getWorld().isDay()
                        && player.getWorld().isSkyVisible(player.getBlockPos())) {
                    player.damage(player.getDamageSources().onFire(), 1.0f);
                    player.sendMessage(Text.literal("§cYou are overheating!"), true);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Network sync
    // -----------------------------------------------------------------------

    private static void sendSyncPacket(ServerPlayerEntity player, SurvivalData data) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(data.thirst);
        buf.writeInt(data.fatigue);
        buf.writeFloat(data.temperature);
        ServerPlayNetworking.send(player, ModPackets.SURVIVAL_SYNC, buf);
    }

    // Public accessor used by the client via mixin or static access
    public static SurvivalData getData(UUID uuid) {
        return SURVIVAL_MAP.get(uuid);
    }
}
