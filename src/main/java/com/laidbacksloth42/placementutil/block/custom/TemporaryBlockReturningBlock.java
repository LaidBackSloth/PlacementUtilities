package com.laidbacksloth42.placementutil.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class TemporaryBlockReturningBlock extends Block {
    public TemporaryBlockReturningBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
                                       FluidState fluid) {
        if (!player.isCreative()) {
            if (player.getInventory().getSlotWithRemainingSpace(new ItemStack(state.getBlock())) != -1
                    || player.getInventory().getFreeSlot() != -1) {
                player.getInventory().add(new ItemStack(state.getBlock()));
            } else {
                player.drop(new ItemStack(state.getBlock()), false);
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
