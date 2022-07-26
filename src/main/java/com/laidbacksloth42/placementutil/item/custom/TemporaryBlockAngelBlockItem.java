package com.laidbacksloth42.placementutil.item.custom;

import com.laidbacksloth42.placementutil.block.ModBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TemporaryBlockAngelBlockItem extends BlockItem {
    public TemporaryBlockAngelBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel,
                                @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        if (Screen.hasControlDown() && Screen.hasShiftDown()) {
            pTooltip.add(Component.translatable("tooltip.placementutil.item.temporary_block_angel"));
        } else {
            pTooltip.add(Component.translatable("tooltip.placementutil.info"));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer,
                                                           @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide) {
            double x = pPlayer.getX() + pPlayer.getLookAngle().x * 4.5;
            double y = pPlayer.getEyePosition().y + pPlayer.getLookAngle().y * 4.5;
            double z = pPlayer.getZ() + pPlayer.getLookAngle().z * 4.5;
            BlockPos pos = new BlockPos(x, y, z);

            if (pLevel.getBlockState(pos).isAir()) {
                pLevel.setBlock(pos, ModBlocks.TEMPORARY_BLOCK_ANGEL.get().defaultBlockState(), 3);
                if (!pPlayer.isCreative()) {
                    if (pUsedHand == InteractionHand.MAIN_HAND) {
                        pPlayer.getInventory().removeFromSelected(false);
                    } else {
                        pPlayer.getInventory().removeItem(Inventory.SLOT_OFFHAND, 1);
                    }
                }
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
