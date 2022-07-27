package com.laidbacksloth42.placementutil.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class Util {
    public static BlockHitResult getPlayerPOVHitResult(Player player) {
        Vec3 vecEye = player.getEyePosition();
        float f2 = Mth.cos(-player.getYRot() * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-player.getYRot() * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-player.getXRot() * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-player.getXRot() * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double reach = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get())).getValue();
        Vec3 vecBlock = vecEye.add((double)f6 * reach, (double)f5 * reach, (double)f7 * reach);
        return player.level.clip(new ClipContext(vecEye, vecBlock, ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE, player));
    }

    public static BlockPos getAdvancedPositionFromBlockHitResult(BlockHitResult blockHitResult, Player player) {
        if (player.level.getBlockState(blockHitResult.getBlockPos()).getMaterial().isReplaceable()) {
            return blockHitResult.getBlockPos();
        } else if (player.level.getBlockState(blockHitResult.getBlockPos().relative(blockHitResult.getDirection()))
                .getMaterial().isReplaceable()){
            return blockHitResult.getBlockPos().relative(blockHitResult.getDirection());
        } else {
            return null;
        }
    }

    public static BlockState getPlacingBlockStateFromPlayer(Player player, Block block) {
        BlockState state = block.defaultBlockState();
        BlockState newState;

        if (state.hasProperty(BlockStateProperties.AXIS)) {
            newState = state.setValue(BlockStateProperties.AXIS, Math.abs(player.getXRot()) > 45 ? Direction.Axis.Y
                    : player.getDirection().getAxis());

        } else if (state.hasProperty(BlockStateProperties.HALF)) {
            newState = state.setValue(BlockStateProperties.HALF,
                    player.getXRot() > 0 ? Half.BOTTOM : Half.TOP);
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, player.getDirection());
            }

        } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            newState = state.setValue(BlockStateProperties.HORIZONTAL_FACING, player.getDirection().getOpposite());

        } else if (state.hasProperty(BlockStateProperties.FACING)) {
            Direction direction = player.getXRot() > 45 ? Direction.DOWN
                    : (player.getXRot() < -45 ? Direction.UP : player.getDirection());
            newState = state.setValue(BlockStateProperties.FACING, direction.getOpposite());

        } else if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
            newState = state.setValue(BlockStateProperties.SLAB_TYPE,
                    player.getXRot() > 0 ? SlabType.BOTTOM : SlabType.TOP);

        } else {
            newState = state;
        }
        return newState;
    }

    public static Supplier<Multimap<Attribute, AttributeModifier>> getRangeModifier(double amount) {
        AttributeModifier rangeAttributeModifier =
                new AttributeModifier(UUID.fromString("a72ae76c-a73b-4eb6-8670-096aa38facd4"), "Range modifier",
                        amount, AttributeModifier.Operation.ADDITION);

        return Suppliers.memoize(() ->
                ImmutableMultimap.of(ForgeMod.REACH_DISTANCE.get(), rangeAttributeModifier));
    }

    public static Supplier<Multimap<Attribute, AttributeModifier>> getRangeTuningModifier(double amount) {
        AttributeModifier rangeTuningAttributeModifier =
                new AttributeModifier(UUID.fromString("91d99148-783a-4bf5-b656-11924dbe77cf"),
                "Range Tuning Modifier", amount, AttributeModifier.Operation.ADDITION);

        return Suppliers.memoize(() ->
                ImmutableMultimap.of(ForgeMod.REACH_DISTANCE.get(), rangeTuningAttributeModifier));
    }
}
