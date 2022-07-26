package com.laidbacksloth42.placementutil.item;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.item.custom.AngelWandItem;
import com.laidbacksloth42.placementutil.item.custom.TemporaryWandItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PlacementUtil.MOD_ID);

    public static final RegistryObject<Item> TEMPORARY_WAND = ModItems.ITEMS.register("temporary_wand",
            () -> new TemporaryWandItem(new Item.Properties().tab(ModCreativeModeTab.PLACEMENTUTIL_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ANGEL_WAND = ModItems.ITEMS.register("angel_wand",
            () -> new AngelWandItem(new Item.Properties().tab(ModCreativeModeTab.PLACEMENTUTIL_TAB).stacksTo(1)));
    public static final RegistryObject<Item> ANGEL_ADDON_RANGE = registerItem("angel_addon_range",
            "tooltip.placementutil.item.angel_addon_range");
    public static final RegistryObject<Item> ANGEL_ADDON_BREAK = registerItem("angel_addon_break",
            "tooltip.placementutil.item.angel_addon_break");
    public static final RegistryObject<Item> ANGEL_ADDON_ROTATE = registerItem("angel_addon_rotate",
            "tooltip.placementutil.item.angel_addon_rotate");

    private static RegistryObject<Item> registerItem(String name, String tooltipKey) {
        return ModItems.ITEMS.register(name, () -> new Item(new Item.Properties()
                .tab(ModCreativeModeTab.PLACEMENTUTIL_TAB)) {
            @Override
            public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel,
                                        @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
                if(Screen.hasControlDown() && Screen.hasShiftDown()) {
                    pTooltip.add(Component.translatable(tooltipKey));
                } else {
                    pTooltip.add(Component.translatable("tooltip.placementutil.info"));
                }
            }
        });
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}