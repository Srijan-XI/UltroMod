package com.example.ultimatemod.items;

import com.example.ultimatemod.inventory.BackpackInventory;
import com.example.ultimatemod.screen.BackpackScreenHandler;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class BackpackItem extends Item {

    public BackpackItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.success(user.getStackInHand(hand));
        if (!(user instanceof ServerPlayerEntity serverPlayer)) return TypedActionResult.pass(user.getStackInHand(hand));

        // Capture the ItemStack reference directly — avoids stale slot lookups if the
        // player swaps items between ticks while the screen factory is being set up.
        final ItemStack backpackStack = serverPlayer.getStackInHand(hand);

        serverPlayer.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (syncId, inv, player) -> {
                    // Load backpack contents from the captured stack's NBT
                    BackpackInventory backpackInv = BackpackInventory.fromStack(backpackStack);
                    return new BackpackScreenHandler(syncId, inv, backpackInv, backpackStack);
                },
                Text.translatable("item.ultimatemod.backpack")
        ));

        return TypedActionResult.success(backpackStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("ultimatemod.tooltip.backpack"));
    }
}
