package com.laidbacksloth42.placementutil.item;

import com.laidbacksloth42.placementutil.block.ModBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModCreativeModeTab {
    public static final CreativeModeTab PLACEMENTUTIL_TAB = new CreativeModeTab("placementutiltab") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ModBlocks.TEMPORARY_BLOCK_DEFAULT.get());
        }
    };
}
