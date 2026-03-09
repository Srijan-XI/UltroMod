package com.example.ultimatemod;

import com.example.ultimatemod.items.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item AUTO_TORCH_PLACER = register("auto_torch_placer",
            new AutoTorchPlacerItem(new Item.Settings().maxCount(1)));

    public static final Item PORTABLE_CRAFTING_TABLE = register("portable_crafting_table",
            new PortableCraftingTableItem(new Item.Settings().maxCount(1)));

    public static final Item INSTANT_SMELTING_STICK = register("instant_smelting_stick",
            new InstantSmeltingStickItem(new Item.Settings().maxCount(1).maxDamage(64)));

    public static final Item TREE_CUTTER_AXE = register("tree_cutter_axe",
            new TreeCutterAxeItem(ToolMaterials.IRON, 6.0f, -3.1f, new Item.Settings().maxDamage(250)));

    public static final Item BACKPACK = register("backpack",
            new BackpackItem(new Item.Settings().maxCount(1)));

    public static final Item COIN = register("coin",
            new Item(new Item.Settings().maxCount(64)));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(UltimateMod.MOD_ID, name), item);
    }

    public static void initialize() {
        // Register the passive auto-torch tick event
        AutoTorchPlacerItem.registerEvents();

        // Register items into the Tools & Utilities group
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(AUTO_TORCH_PLACER);
            entries.add(PORTABLE_CRAFTING_TABLE);
            entries.add(INSTANT_SMELTING_STICK);
            entries.add(TREE_CUTTER_AXE);
            entries.add(BACKPACK);
        });

        // Register coin in combat group (dropped by zombies)
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(COIN);
        });
    }
}
