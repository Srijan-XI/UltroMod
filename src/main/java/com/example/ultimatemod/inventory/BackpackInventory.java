package com.example.ultimatemod.inventory;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class BackpackInventory extends SimpleInventory {

    public static final int SIZE = 27; // 3 rows × 9 columns

    public BackpackInventory() {
        super(SIZE);
    }

    // ---------------------------------------------------------------
    // NBT serialization
    // ---------------------------------------------------------------

    public void readFromNbt(NbtCompound nbt) {
        NbtList list = nbt.getList("Items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); i++) {
            NbtCompound entry = list.getCompound(i);
            int slot = entry.getByte("Slot") & 0xFF;
            if (slot < SIZE) {
                setStack(slot, ItemStack.fromNbt(entry));
            }
        }
    }

    public NbtCompound writeToNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList list = new NbtList();
        for (int i = 0; i < SIZE; i++) {
            ItemStack stack = getStack(i);
            if (!stack.isEmpty()) {
                NbtCompound entry = new NbtCompound();
                entry.putByte("Slot", (byte) i);
                stack.writeNbt(entry);
                list.add(entry);
            }
        }
        nbt.put("Items", list);
        return nbt;
    }

    // ---------------------------------------------------------------
    // Factory and save helpers
    // ---------------------------------------------------------------

    /**
     * Creates a BackpackInventory loaded from the item stack's NBT data.
     */
    public static BackpackInventory fromStack(ItemStack backpackStack) {
        BackpackInventory inv = new BackpackInventory();
        NbtCompound root = backpackStack.getOrCreateNbt();
        if (root.contains("BackpackInventory", NbtElement.COMPOUND_TYPE)) {
            inv.readFromNbt(root.getCompound("BackpackInventory"));
        }
        return inv;
    }

    /**
     * Saves the current inventory content back into the item stack's NBT data.
     */
    public void saveToStack(ItemStack backpackStack) {
        NbtCompound root = backpackStack.getOrCreateNbt();
        root.put("BackpackInventory", writeToNbt());
    }
}
