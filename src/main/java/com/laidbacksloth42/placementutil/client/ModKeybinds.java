package com.laidbacksloth42.placementutil.client;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeybinds {
    public static KeyMapping angelWandRotate;
    public static KeyMapping angelWandReachUp;
    public static KeyMapping angelWandReachDown;
    public static KeyMapping angelWandReachScroll;

    public static void init() {
        angelWandRotate = registerKey("angel_wand_rotate", InputConstants.KEY_R);
        angelWandReachUp = registerKey("angel_wand_reach_up", InputConstants.KEY_V);
        angelWandReachDown = registerKey("angel_wand_reach_down", InputConstants.KEY_X);
        angelWandReachScroll = registerKey("angel_wand_reach_scroll", InputConstants.KEY_LCONTROL);
    }

    private static KeyMapping registerKey(String name, int keycode) {
        return new KeyMapping("key." + PlacementUtil.MOD_ID + "." + name, keycode,
                "key.placementutil.category");
    }

    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        event.register(angelWandRotate);
        event.register(angelWandReachUp);
        event.register(angelWandReachDown);
        event.register(angelWandReachScroll);
    }
}
