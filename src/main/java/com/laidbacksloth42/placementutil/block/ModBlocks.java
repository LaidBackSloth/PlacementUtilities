package com.laidbacksloth42.placementutil.block;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.block.custom.TemporaryBlockReturningBlock;
import com.laidbacksloth42.placementutil.block.custom.TemporaryBlockSelfDestructingBlock;
import com.laidbacksloth42.placementutil.item.ModCreativeModeTab;
import com.laidbacksloth42.placementutil.item.ModItems;
import com.laidbacksloth42.placementutil.item.custom.TemporaryBlockAngelBlockItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PlacementUtil.MOD_ID);

    public static final RegistryObject<Block> TEMPORARY_BLOCK_DEFAULT = registerBlock("temporary_block_default",
            () -> new Block(BlockBehaviour.Properties.of(Material.WOOD).instabreak().noCollission()),
            "tooltip.placementutil.block.temporary_block_default");

    public static final RegistryObject<Block> TEMPORARY_BLOCK_RETURNING =
            registerBlock("temporary_block_returning", () -> new TemporaryBlockReturningBlock(
                    BlockBehaviour.Properties.of(Material.WOOD).instabreak().noCollission()),
            "tooltip.placementutil.block.temporary_block_returning");

    public static final RegistryObject<Block> TEMPORARY_BLOCK_SELF_DESTRUCTING =
            registerBlock("temporary_block_self_destructing", () -> new TemporaryBlockSelfDestructingBlock(
                    BlockBehaviour.Properties.of(Material.WOOD).instabreak().noCollission()),
                    "tooltip.placementutil.block.temporary_block_self_destructing");

    public static final RegistryObject<Block> TEMPORARY_BLOCK_ANGEL = BLOCKS.register("temporary_block_angel",
            () -> new TemporaryBlockReturningBlock(BlockBehaviour.Properties.of(Material.WOOD).instabreak()
                    .noCollission()));
    private static final RegistryObject<Item> TEMPORARY_BLOCK_ANGEL_ITEM =
            ModItems.ITEMS.register("temporary_block_angel", () -> new TemporaryBlockAngelBlockItem(
                    TEMPORARY_BLOCK_ANGEL.get(), new Item.Properties().tab(ModCreativeModeTab.PLACEMENTUTIL_TAB)));


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block,
                                                                     String tooltipKey) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tooltipKey);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, String tooltipKey) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ModCreativeModeTab.PLACEMENTUTIL_TAB)) {
            @Override
            public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel,
                                        @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
                if (Screen.hasControlDown() && Screen.hasShiftDown()) {
                    pTooltip.add(new TranslatableComponent(tooltipKey));
                } else {
                    pTooltip.add(new TranslatableComponent("tooltip.placementutil.info"));
                }
            }
        });
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
