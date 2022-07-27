package com.laidbacksloth42.placementutil.networking;

import com.laidbacksloth42.placementutil.PlacementUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PlacementUtil.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        int index = 0;
        INSTANCE.messageBuilder(ServerboundAngelWandRotatingPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundAngelWandRotatingPacket::encode).decoder(ServerboundAngelWandRotatingPacket::new)
                .consumer(ServerboundAngelWandRotatingPacket::handle).add();
        INSTANCE.messageBuilder(ServerboundAngelWandReachPacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ServerboundAngelWandReachPacket::encode).decoder(ServerboundAngelWandReachPacket::new)
                .consumer(ServerboundAngelWandReachPacket::handle).add();
    }
}