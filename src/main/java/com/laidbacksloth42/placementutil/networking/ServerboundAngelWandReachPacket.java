package com.laidbacksloth42.placementutil.networking;

import com.google.common.collect.Multimap;
import com.laidbacksloth42.placementutil.util.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerboundAngelWandReachPacket {
    private static final UUID RANGE_TUNING_MODIFIER_UUID = UUID.fromString("91d99148-783a-4bf5-b656-11924dbe77cf");
    public final boolean reverse;

    public ServerboundAngelWandReachPacket(boolean reverse) {
        this.reverse = reverse;
    }

    public ServerboundAngelWandReachPacket(FriendlyByteBuf buffer) {
        this(buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(reverse);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            double modifierValue = player.getAttributes().hasModifier(ForgeMod.REACH_DISTANCE.get(),
                    RANGE_TUNING_MODIFIER_UUID) ? player.getAttributes().getModifierValue(ForgeMod.REACH_DISTANCE.get(),
                    RANGE_TUNING_MODIFIER_UUID) : 0;
            double baseValue = Objects.requireNonNull(player.getAttribute(ForgeMod.REACH_DISTANCE.get())).getValue();
            double amount = this.reverse ? (baseValue > 2 ? modifierValue - 1 : 1 + modifierValue - baseValue)
                    : (modifierValue < -1 ? modifierValue + 1 : 0);

            Supplier<Multimap<Attribute, AttributeModifier>> rangeTuningModifier = Util.getRangeTuningModifier(amount);

            if (player.getAttributes().hasModifier(ForgeMod.REACH_DISTANCE.get(), RANGE_TUNING_MODIFIER_UUID)) {
                player.getAttributes().removeAttributeModifiers(rangeTuningModifier.get());
            }
            player.getPersistentData().putDouble("placementutilAngelModifier", amount);
            player.getAttributes().addTransientAttributeModifiers(rangeTuningModifier.get());
        });
        ctx.get().setPacketHandled(true);
    }
}
