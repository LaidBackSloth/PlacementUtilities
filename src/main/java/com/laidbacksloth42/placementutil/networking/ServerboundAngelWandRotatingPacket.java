package com.laidbacksloth42.placementutil.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundAngelWandRotatingPacket {
    public final BlockPos blockPos;

    public ServerboundAngelWandRotatingPacket(BlockPos pos) {
        this.blockPos = pos;
    }

    public ServerboundAngelWandRotatingPacket(FriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(blockPos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            BlockState state = player.level.getBlockState(this.blockPos);

            if (!canRotate(state)) {
                return;
            }

            BlockState newState = null;

            if (state.hasProperty(BlockStateProperties.AXIS)) {
                Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                newState = state.setValue(BlockStateProperties.AXIS, axis == Direction.Axis.X ? Direction.Axis.Y
                        : axis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.X);

            } else if (state.hasProperty(BlockStateProperties.HALF)) {
                if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && !player.isShiftKeyDown()) {
                    if (state.getValue(BlockStateProperties.HALF) == Half.BOTTOM) {
                        if (state.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.WEST) {
                            newState = state.setValue(BlockStateProperties.HALF, Half.TOP);
                        } else {
                            newState = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                                    state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
                        }
                    } else {
                        if (state.getValue(BlockStateProperties.HORIZONTAL_FACING) == Direction.NORTH) {
                            newState = state.setValue(BlockStateProperties.HALF, Half.BOTTOM);
                        } else {
                            newState = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                                    state.getValue(BlockStateProperties.HORIZONTAL_FACING).getCounterClockWise());
                        }
                    }
                } else {
                    newState = state.setValue(BlockStateProperties.HALF,
                            state.getValue(BlockStateProperties.HALF) == Half.BOTTOM ? Half.TOP : Half.BOTTOM);
                }

            } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                if (player.isShiftKeyDown()) {
                    newState = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                            state.getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite());
                } else {
                    newState = state;
                    for (int i = 0; i < 4; i++) {
                        newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING,
                                newState.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise());
                        if (newState.canSurvive(player.level, this.blockPos)) {
                            break;
                        }
                    }
                }

            } else if (state.hasProperty(BlockStateProperties.FACING)) {
                Direction direction = state.getValue(BlockStateProperties.FACING);
                if (player.isShiftKeyDown()) {
                    newState = state.setValue(BlockStateProperties.FACING, direction.getOpposite());
                } else {
                    newState = state.setValue(BlockStateProperties.FACING, direction == Direction.WEST ? Direction.UP
                            : direction == Direction.UP ? Direction.DOWN : direction == Direction.DOWN ? Direction.NORTH
                            : direction.getClockWise());
                }

            } else if (state.hasProperty(BlockStateProperties.SLAB_TYPE)) {
                if (state.getValue(BlockStateProperties.SLAB_TYPE) != SlabType.DOUBLE) {
                    newState = state.setValue(BlockStateProperties.SLAB_TYPE,
                            state.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM ? SlabType.TOP
                                    : SlabType.BOTTOM);
                }
            }

            if (newState != null) {
                if (newState.canSurvive(player.level, this.blockPos)) {
                    player.level.setBlock(this.blockPos, newState, 3);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private boolean canRotate(BlockState state) {
        Block block = state.getBlock();

        if (block instanceof PistonHeadBlock || block instanceof BedBlock) {
            return false;
        }

        if (state.hasProperty(BlockStateProperties.EXTENDED)) {
            return !state.getValue(BlockStateProperties.EXTENDED);
        }

        if (state.hasProperty(BlockStateProperties.CHEST_TYPE)) {
            return state.getValue(BlockStateProperties.CHEST_TYPE) == ChestType.SINGLE;
        }

        return true;
    }
}
