package com.example.ultimatemod.screen;

import com.example.ultimatemod.inventory.BackpackInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * Extends vanilla 9x3 container handler so the vanilla GenericContainerScreen
 * renders it on the client — no custom screen or ScreenHandlerType registration needed.
 * The only addition is saving the backpack inventory to NBT when closed.
 */
public class BackpackScreenHandler extends GenericContainerScreenHandler {

    private final BackpackInventory backpackInventory;
    private final ItemStack backpackStack;

    public BackpackScreenHandler(int syncId, PlayerInventory playerInventory,
                                 BackpackInventory backpackInventory, ItemStack backpackStack) {
        // Use vanilla GENERIC_9X3 — no custom ScreenHandlerType registration needed
        super(ScreenHandlerType.GENERIC_9X3, syncId, playerInventory, backpackInventory, 3);
        this.backpackInventory = backpackInventory;
        this.backpackStack = backpackStack;
        backpackInventory.onOpen(playerInventory.player);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        // Persist inventory back into the item stack NBT
        backpackInventory.saveToStack(backpackStack);
    }
}
