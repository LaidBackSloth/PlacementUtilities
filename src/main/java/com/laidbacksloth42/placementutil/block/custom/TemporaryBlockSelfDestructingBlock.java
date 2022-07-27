package com.laidbacksloth42.placementutil.block.custom;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.block.ModBlocks;
import com.laidbacksloth42.placementutil.config.ModCommonConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID)
public class TemporaryBlockSelfDestructingBlock extends Block {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 200);
    public static List<Level> levels = new ArrayList<>();
    public static List<BlockPos> poss = new ArrayList<>();

    public TemporaryBlockSelfDestructingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    @Override
    public void onPlace(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pOldState,
                        boolean pIsMoving) {
        if (pState.getValue(AGE) > 0 || ModCommonConfigs.TEMPORARY_WAND_ALTERNATIVE.get()) {
            return;
        }
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) == null) {
                levels.set(i, pLevel);
                poss.set(i, pPos);
                return;
            }
        }
        levels.add(pLevel);
        poss.add(pPos);
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) == null) {
                continue;
            }

            Level level = levels.get(i);
            BlockPos pos = poss.get(i);

            if (level.getBlockState(pos).is(ModBlocks.TEMPORARY_BLOCK_SELF_DESTRUCTING.get())) {
                levels.set(i, null);
                poss.set(i, null);
                level.destroyBlock(pos, false);
            }
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (ModCommonConfigs.TEMPORARY_WAND_ALTERNATIVE.get()) {
            return;
        }

        for (int i = 0; i < levels.size(); i++) {
            if (levels.get(i) == null) {
                continue;
            }

            Level level = levels.get(i);
            BlockPos pos = poss.get(i);

            if (!level.getBlockState(pos).is(ModBlocks.TEMPORARY_BLOCK_SELF_DESTRUCTING.get())) {
                levels.set(i, null);
                poss.set(i, null);
                continue;
            }

            int currentState = level.getBlockState(pos).getValue(AGE);

            if (currentState < 200) {
                level.setBlock(pos, level.getBlockState(pos).setValue(AGE, currentState + 1), 18);
            } else {
                levels.set(i, null);
                poss.set(i, null);
                level.destroyBlock(pos, false);
            }
        }

        for (int i = 0; i < levels.size(); i++) {
            int index = levels.size() - 1 - i;
            if (levels.get(index) == null) {
                levels.remove(index);
                poss.remove(index);
            } else {
                break;
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState pState) {
        return ModCommonConfigs.TEMPORARY_WAND_ALTERNATIVE.get();
    }

    @Override
    public void randomTick(@NotNull BlockState pState, @NotNull ServerLevel pLevel, @NotNull BlockPos pPos,
                           @NotNull Random pRandom) {
        pLevel.destroyBlock(pPos, false);
        super.randomTick(pState, pLevel, pPos, pRandom);
    }
}