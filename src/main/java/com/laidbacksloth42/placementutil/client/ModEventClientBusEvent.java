package com.laidbacksloth42.placementutil.client;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.block.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventClientBusEvent {
    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_BLOCK_DEFAULT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_BLOCK_RETURNING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_BLOCK_SELF_DESTRUCTING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.TEMPORARY_BLOCK_ANGEL.get(), RenderType.cutout());

        ModKeybinds.init();
    }
}
