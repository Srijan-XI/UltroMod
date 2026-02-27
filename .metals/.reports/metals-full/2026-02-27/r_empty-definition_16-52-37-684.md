error id: file:///P:/CODE-XI/MincraftMods/Mod1/src/main/java/com/example/ultimatemod/drops/BetterDropsHandler.java:net/fabricmc/fabric/api/event/player/PlayerBlockBreakEvents#
file:///P:/CODE-XI/MincraftMods/Mod1/src/main/java/com/example/ultimatemod/drops/BetterDropsHandler.java
empty definition using pc, found symbol in pc: net/fabricmc/fabric/api/event/player/PlayerBlockBreakEvents#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 128
uri: file:///P:/CODE-XI/MincraftMods/Mod1/src/main/java/com/example/ultimatemod/drops/BetterDropsHandler.java
text:
```scala
package com.example.ultimatemod.drops;

import com.example.ultimatemod.ModItems;
import net.fabricmc.fabric.api.event.player.@@PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableSource;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

public class BetterDropsHandler {

    // Mapping: leaves loot-table path -> corresponding sapling item
    private static final Map<String, net.minecraft.item.Item> LEAVES_TO_SAPLING = Map.of(
            "blocks/oak_leaves",      Items.OAK_SAPLING,
            "blocks/birch_leaves",    Items.BIRCH_SAPLING,
            "blocks/spruce_leaves",   Items.SPRUCE_SAPLING,
            "blocks/jungle_leaves",   Items.JUNGLE_SAPLING,
            "blocks/acacia_leaves",   Items.ACACIA_SAPLING,
            "blocks/dark_oak_leaves", Items.DARK_OAK_SAPLING,
            "blocks/mangrove_leaves", Items.MANGROVE_PROPAGULE,
            "blocks/cherry_leaves",   Items.CHERRY_SAPLING
    );

    public static void initialize() {
        registerZombieCoinDrops();
        registerExtraSaplingDrops();
        registerMiningXpBoost();
    }

    // ------------------------------------------------------------------
    // Zombies drop coins (50 % chance, 1-3 coins per roll)
    // ------------------------------------------------------------------

    private static void registerZombieCoinDrops() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!id.equals(new Identifier("minecraft", "entities/zombie"))) return;

            tableBuilder.pool(LootPool.builder()
                    .rolls(UniformLootNumberProvider.create(1, 3))
                    .with(ItemEntry.builder(ModItems.COIN))
                    .conditionally(RandomChanceLootCondition.builder(0.5f)));
        });
    }

    // ------------------------------------------------------------------
    // Trees drop extra saplings (30 % chance per leaves block)
    // ------------------------------------------------------------------

    private static void registerExtraSaplingDrops() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!id.getNamespace().equals("minecraft")) return;

            net.minecraft.item.Item sapling = LEAVES_TO_SAPLING.get(id.getPath());
            if (sapling == null) return;

            tableBuilder.pool(LootPool.builder()
                    .rolls(ConstantLootNumberProvider.create(1))
                    .with(ItemEntry.builder(sapling))
                    .conditionally(RandomChanceLootCondition.builder(0.3f)));
        });
    }

    // ------------------------------------------------------------------
    // Mining ores gives bonus XP
    // ------------------------------------------------------------------

    private static void registerMiningXpBoost() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

            int bonusXp = getOreXpBonus(state.getBlock());
            if (bonusXp > 0) {
                serverPlayer.addExperience(bonusXp);
            }
        });
    }

    private static int getOreXpBonus(Block block) {
        if (isInTag(block, BlockTags.COAL_ORES))     return 2;
        if (isInTag(block, BlockTags.IRON_ORES))     return 3;
        if (isInTag(block, BlockTags.GOLD_ORES))     return 4;
        if (isInTag(block, BlockTags.LAPIS_ORES))    return 4;
        if (isInTag(block, BlockTags.REDSTONE_ORES)) return 3;
        if (isInTag(block, BlockTags.EMERALD_ORES))  return 6;
        if (isInTag(block, BlockTags.DIAMOND_ORES))  return 7;
        if (isInTag(block, BlockTags.COPPER_ORES))   return 2;
        return 0;
    }

    private static boolean isInTag(Block block, net.minecraft.registry.tag.TagKey<Block> tag) {
        return block.getDefaultState().isIn(tag);
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: net/fabricmc/fabric/api/event/player/PlayerBlockBreakEvents#