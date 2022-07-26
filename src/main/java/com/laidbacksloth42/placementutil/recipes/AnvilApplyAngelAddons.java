package com.laidbacksloth42.placementutil.recipes;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.config.ModCommonConfigs;
import com.laidbacksloth42.placementutil.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilApplyAngelAddons {
    @SubscribeEvent
    public static void anvilEvent(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        if (left.getItem() != ModItems.ANGEL_WAND.get()) {
            return;
        }
        ItemStack right = event.getRight();
        ItemStack output = left.copy();
        int materialCost = 1;
        int cost = 1;
        CompoundTag nbtData = left.getOrCreateTag().copy();
        int rangeValue = left.getOrCreateTag().getInt("placementutil_angel_range");
        boolean breakValue = left.getOrCreateTag().getBoolean("placementutil_angel_break");
        boolean rotateValue = left.getOrCreateTag().getBoolean("placementutil_angel_rotate");
        int maxRange = ModCommonConfigs.ANGEL_MODIFIER_RANGE_MAX.get();

        if (right.getItem() == ModItems.ANGEL_ADDON_BREAK.get() && !breakValue) {
            nbtData.putBoolean("placementutil_angel_break", true);
        } else if (right.getItem() == ModItems.ANGEL_ADDON_ROTATE.get() && !rotateValue) {
            nbtData.putBoolean("placementutil_angel_rotate", true);
        } else if (right.getItem() == ModItems.ANGEL_ADDON_RANGE.get() && rangeValue < maxRange){
            if (right.getCount() >= maxRange - rangeValue) {
                nbtData.putInt("placementutil_angel_range", maxRange);
                materialCost = maxRange - rangeValue;
                cost = maxRange - rangeValue;
            } else {
                nbtData.putInt("placementutil_angel_range", rangeValue + right.getCount());
                materialCost = right.getCount();
                cost = right.getCount();
            }
        } else {
            return;
        }

        output.setTag(nbtData);
        event.setMaterialCost(materialCost);
        event.setCost(cost);
        event.setOutput(output);
    }
}