package com.example.ultimatemod.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class TreeCutterAxeItem extends AxeItem {

    private static final int MAX_LOGS = 64;

    public TreeCutterAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        // Only run tree-cutting on the server for log blocks
        if (!world.isClient && state.isIn(BlockTags.LOGS)) {
            cutTree(stack, world, pos, miner);
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    private void cutTree(ItemStack axe, World world, BlockPos brokenPos, LivingEntity miner) {
        // BFS from the neighbors of the just-broken block to find all connected logs.
        // 'visited' tracks positions already enqueued so no position is queued twice.
        Set<BlockPos> logs    = new LinkedHashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue  = new LinkedList<>();

        for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
            BlockPos neighbor = brokenPos.offset(dir);
            if (visited.add(neighbor)) queue.add(neighbor);
        }

        while (!queue.isEmpty() && logs.size() < MAX_LOGS) {
            BlockPos current = queue.poll();
            if (!world.getBlockState(current).isIn(BlockTags.LOGS)) continue;

            logs.add(current);
            for (net.minecraft.util.math.Direction dir : net.minecraft.util.math.Direction.values()) {
                BlockPos neighbor = current.offset(dir);
                // Only enqueue if not already visited AND we haven't hit the cap yet
                if (visited.add(neighbor) && logs.size() < MAX_LOGS) {
                    queue.add(neighbor);
                }
            }
        }

        if (logs.isEmpty()) return;

        // Show feedback
        if (miner instanceof PlayerEntity player) {
            player.sendMessage(Text.literal("§aTree Cutter: felling " + logs.size() + " logs!"), true);
        }

        // Break all found logs, awarding drops and applying durability
        for (BlockPos logPos : logs) {
            world.breakBlock(logPos, true, miner);
            if (miner instanceof ServerPlayerEntity serverPlayer) {
                axe.damage(1, serverPlayer, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("ultimatemod.tooltip.tree_cutter_axe"));
    }
}
