package com.laidbacksloth42.placementutil;

import com.laidbacksloth42.placementutil.block.ModBlocks;
import com.laidbacksloth42.placementutil.config.ModCommonConfigs;
import com.laidbacksloth42.placementutil.item.ModItems;
import com.laidbacksloth42.placementutil.networking.ModNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PlacementUtil.MOD_ID)
public class PlacementUtil {
    public static final String MOD_ID = "placementutil";

    public PlacementUtil() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);

        eventBus.addListener(this::commonSetup);

        ModLoadingContext.get().
                registerConfig(ModConfig.Type.SERVER, ModCommonConfigs.SPEC, "placementutil-server.toml");

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::init);
    }
}
