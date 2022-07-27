package com.laidbacksloth42.placementutil.item.custom;

import com.laidbacksloth42.placementutil.block.ModBlocks;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TemporaryWandItem extends Item {
    public TemporaryWandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip,
                                @NotNull TooltipFlag pFlag) {
        if(Screen.hasControlDown() && Screen.hasShiftDown()) {
            pTooltip.add(new TranslatableComponent("tooltip.placementutil.item.temporary_wand"));
        } else {
            pTooltip.add(new TranslatableComponent("tooltip.placementutil.info"));
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (!context.getLevel().isClientSide() && context.getLevel().isEmptyBlock(context.getClickedPos()
                .relative(context.getClickedFace()))) {
            context.getLevel().setBlock(context.getClickedPos().relative(context.getClickedFace()),
                    ModBlocks.TEMPORARY_BLOCK_SELF_DESTRUCTING.get().defaultBlockState(), 3);
        }
        return super.onItemUseFirst(stack, context);
    }
}
