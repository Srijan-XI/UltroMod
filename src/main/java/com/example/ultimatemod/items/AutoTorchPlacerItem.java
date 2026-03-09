package com.example.ultimatemod.items;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.List;

public class AutoTorchPlacerItem extends Item {

    public AutoTorchPlacerItem(Settings settings) {
        super(settings);
    }

    /**
     * Registers the server-tick event that auto-places torches.
     * Call once during mod initialization.
     */
    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(AutoTorchPlacerItem::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        // Run every 20 ticks (1 second). Use server tick count instead of a static
        // counter to avoid the shared-counter bug when multiple worlds run simultaneously.
        if (server.getTicks() % 20 != 0) return;

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (!hasAutoTorchPlacer(player)) continue;

            World world = player.getWorld();
            BlockPos feet = player.getBlockPos();

            // Only place if block light is very low
            int blockLight = world.getLightLevel(LightType.BLOCK, feet);
            if (blockLight > 6) continue;

            // Find a torch in the player's inventory
            PlayerInventory inv = player.getInventory();
            int torchSlot = inv.getSlotWithStack(new ItemStack(Items.TORCH));
            if (torchSlot == -1) continue;

            // Try to place on or near the player
            BlockPos placePos = findPlacementPos(world, feet);
            if (placePos == null) continue;

            // Place the torch and consume one from inventory
            world.setBlockState(placePos, Blocks.TORCH.getDefaultState());
            inv.getStack(torchSlot).decrement(1);
        }
    }

    private static BlockPos findPlacementPos(World world, BlockPos feet) {
        // Check the feet position first, then adjacent positions
        BlockPos[] candidates = {
                feet,
                feet.north(), feet.south(), feet.east(), feet.west()
        };
        for (BlockPos pos : candidates) {
            if (world.getBlockState(pos).isAir()
                    && world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
                return pos;
            }
        }
        return null;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("ultimatemod.tooltip.auto_torch_placer"));
    }

    /** Returns true if the player has an Auto Torch Placer anywhere in their inventory */
    private static boolean hasAutoTorchPlacer(PlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).getItem() instanceof AutoTorchPlacerItem) return true;
        }
        return false;
    }
}
