package com.example.ultimatemod.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class InstantSmeltingStickItem extends Item {

    public InstantSmeltingStickItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));

        // Smelt the item in the offhand (or mainhand companion)
        Hand targetHand = (hand == Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack targetStack = user.getStackInHand(targetHand);

        if (targetStack.isEmpty()) {
            user.sendMessage(Text.literal("§cHold an item to smelt in your other hand!"), true);
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        SimpleInventory inv = new SimpleInventory(targetStack.copy());
        Optional<SmeltingRecipe> recipe =
                world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, inv, world);

        if (recipe.isEmpty()) {
            user.sendMessage(Text.literal("§cThat item cannot be smelted!"), true);
            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        ItemStack result = recipe.get().getOutput(world.getRegistryManager()).copy();
        int smeltCount = targetStack.getCount();
        result.setCount(smeltCount);

        // Replace the target hand with smelted result
        user.setStackInHand(targetHand, result);

        // Award experience
        float xp = recipe.get().getExperience() * smeltCount;
        user.addExperience((int) xp);

        // Sound effect
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                SoundCategory.PLAYERS, 1.0f, 1.0f);

        // Damage the stick
        ItemStack stick = user.getStackInHand(hand);
        stick.damage(1, user, p -> p.sendToolBreakStatus(hand));

        user.sendMessage(Text.literal("§6Smelted " + smeltCount + "x " +
                result.getName().getString() + "!"), true);

        return TypedActionResult.success(stick);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("ultimatemod.tooltip.instant_smelting_stick"));
    }
}
