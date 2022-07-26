package com.laidbacksloth42.placementutil.item.custom;

import com.google.common.collect.Multimap;
import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.config.ModCommonConfigs;
import com.laidbacksloth42.placementutil.item.ModItems;
import com.laidbacksloth42.placementutil.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID)
public class AngelWandItem extends Item {
    private static final UUID RANGE_TUNING_MODIFIER_UUID = UUID.fromString("91d99148-783a-4bf5-b656-11924dbe77cf");

    public AngelWandItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip,
                                @NotNull TooltipFlag pFlag) {
        if (Screen.hasControlDown() && Screen.hasShiftDown()) {
            pTooltip.add(Component.translatable("tooltip.placementutil.item.angel_wand"));
        } else {
            int rangeValue = pStack.getOrCreateTag().getInt("placementutil_angel_range");
            boolean breakValue = pStack.getOrCreateTag().getBoolean("placementutil_angel_break");
            boolean rotateValue = pStack.getOrCreateTag().getBoolean("placementutil_angel_rotate");
            int maxRange = ModCommonConfigs.ANGEL_MODIFIER_RANGE_MAX.get();
            String angelRange = "Range: " + (rangeValue == 0 ? "§c" : rangeValue == maxRange ? "§a" : "§e")
                    + rangeValue + " / " + maxRange;
            String angelBreak = "Break: " + (breakValue ? "§a" : "§c") + breakValue;
            String angelRotate = "Rotate: " + (rotateValue ? "§a" : "§c") + rotateValue;
            pTooltip.add(Component.literal(angelRange));
            pTooltip.add(Component.literal(angelBreak));
            pTooltip.add(Component.literal(angelRotate));
            pTooltip.add(Component.empty());
            pTooltip.add(Component.translatable("tooltip.placementutil.info"));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer,
                                                           @NotNull InteractionHand pUsedHand) {
        if (!pLevel.isClientSide && pUsedHand == InteractionHand.OFF_HAND) {
            Item held = pPlayer.getMainHandItem().getItem();
            if (!(held instanceof BlockItem)) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            BlockHitResult blockHitResult = Util.getPlayerPOVHitResult(pPlayer);
            BlockPos pos = Util.getAdvancedPositionFromBlockHitResult(blockHitResult, pPlayer);
            if (pos == null) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            Block block = ((BlockItem) held).getBlock();
            if (block instanceof DoorBlock || block instanceof BedBlock
                    || pPlayer.level.getBlockState(pos).getBlock() == block) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            BlockState newState = Util.getPlacingBlockStateFromPlayer(pPlayer, block);
            if (!newState.canSurvive(pPlayer.level, pos)) {
                return super.use(pLevel, pPlayer, pUsedHand);
            }

            pLevel.setBlock(pos, newState, 3);

            if (!pPlayer.isCreative()) {
                pPlayer.getInventory().removeFromSelected(false);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @SubscribeEvent
    public static void holdingAngelWandIncreasesRangeAndGivesMiningFatigue(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (player.level.isClientSide) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();
        boolean holdingAngelWand = player.getOffhandItem().getItem() == ModItems.ANGEL_WAND.get();
        boolean wasHoldingAngelWand = persistentData.contains("placementutilAngelWand");

        int amount = 0;
        if (player.getOffhandItem().getItem() == ModItems.ANGEL_WAND.get()) {
            amount = player.getOffhandItem().getOrCreateTag().getInt("placementutil_angel_range");
            if (persistentData.getInt("placementutil_angel_range_last") != amount) {
                holdingAngelWand = false;
                wasHoldingAngelWand = true;
                persistentData.putInt("placementutil_angel_range_last", amount);
            }
        }

        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Util.getRangeModifier(amount);

        if (!persistentData.contains("placementutilAngelModifier")) {
            persistentData.putDouble("placementutilAngelModifier", 0);
        }
        Supplier<Multimap<Attribute, AttributeModifier>> rangeTuningModifier =
                Util.getRangeTuningModifier(persistentData.getDouble("placementutilAngelModifier"));

        if (holdingAngelWand != wasHoldingAngelWand) {
            if (!holdingAngelWand) {
                player.getAttributes().removeAttributeModifiers(rangeModifier.get());
                if (player.getAttributes().hasModifier(ForgeMod.REACH_DISTANCE.get(), RANGE_TUNING_MODIFIER_UUID)) {
                    player.getAttributes().removeAttributeModifiers(rangeTuningModifier.get());
                }
                persistentData.remove("placementutilAngelWand");
            } else {
                player.getAttributes().addTransientAttributeModifiers(rangeModifier.get());
                if (persistentData.contains("placementutilAngelModifier")) {
                    player.getAttributes().addTransientAttributeModifiers(rangeTuningModifier.get());
                }
                persistentData.putBoolean("placementutilAngelWand", true);
            }
        }

        if (player.getOffhandItem().getItem() == ModItems.ANGEL_WAND.get()
                && !player.getOffhandItem().getOrCreateTag().getBoolean("placementutil_angel_break")) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 2, false,
                    false, false));
        }
    }

    @SubscribeEvent
    public static void addReachToJoiningPlayersHoldingAngelWand(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level.isClientSide) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();

        int amount = 0;
        if (player.getOffhandItem().getItem() == ModItems.ANGEL_WAND.get()) {
            amount = player.getOffhandItem().getOrCreateTag().getInt("placementutil_angel_range");
        }

        Supplier<Multimap<Attribute, AttributeModifier>> rangeModifier = Util.getRangeModifier(amount);

        if (!persistentData.contains("placementutilAngelModifier")) {
            persistentData.putDouble("placementutilAngelModifier", 0);
        }

        Supplier<Multimap<Attribute, AttributeModifier>> rangeTuningModifier =
                Util.getRangeTuningModifier(persistentData.getDouble("placementutilAngelModifier"));

        if (persistentData.contains("placementutilAngelWand")) {
            player.getAttributes().addTransientAttributeModifiers(rangeModifier.get());
            if (persistentData.getDouble("placementutilAngelModifier") != 0) {
                player.getAttributes().addTransientAttributeModifiers(rangeTuningModifier.get());
            }
        }
    }
}