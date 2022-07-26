package com.laidbacksloth42.placementutil.client;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.item.ModItems;
import com.laidbacksloth42.placementutil.networking.ModNetworking;
import com.laidbacksloth42.placementutil.networking.ServerboundAngelWandReachPacket;
import com.laidbacksloth42.placementutil.networking.ServerboundAngelWandRotatingPacket;
import com.laidbacksloth42.placementutil.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEventClientBusEvent {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getOffhandItem().getItem() != ModItems.ANGEL_WAND.get()) {
            return;
        }

        if (ModKeybinds.angelWandRotate.consumeClick()
                && player.getOffhandItem().getOrCreateTag().getBoolean("placementutil_angel_rotate")) {
            BlockHitResult blockhitresult = Util.getPlayerPOVHitResult(player);
            ModNetworking.INSTANCE.
                    sendToServer(new ServerboundAngelWandRotatingPacket(blockhitresult.getBlockPos()));
        }
        if (ModKeybinds.angelWandReachUp.consumeClick()) {
            ModNetworking.INSTANCE.sendToServer(new ServerboundAngelWandReachPacket(false));
        }
        if (ModKeybinds.angelWandReachDown.consumeClick()) {
            ModNetworking.INSTANCE.sendToServer(new ServerboundAngelWandReachPacket(true));
        }
    }

    @SubscribeEvent
    public static void mouseWheelInput(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.getOffhandItem().getItem() != ModItems.ANGEL_WAND.get()) {
            return;
        }

        if (ModKeybinds.angelWandReachScroll.isDown()) {
            ModNetworking.INSTANCE.sendToServer(new ServerboundAngelWandReachPacket(event.getScrollDelta() < 0));
            event.setCanceled(true);
        }
    }
}
