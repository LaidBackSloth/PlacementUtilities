package com.laidbacksloth42.placementutil.client;

import com.laidbacksloth42.placementutil.PlacementUtil;
import com.laidbacksloth42.placementutil.item.ModItems;
import com.laidbacksloth42.placementutil.util.Util;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(modid = PlacementUtil.MOD_ID, value = Dist.CLIENT)
public class AngelWandBlockPlacingMarker {
    @SubscribeEvent
    public static void onRenderWorldEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS
                || event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        if (!camera.isInitialized()) {
            return;
        }

        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        gameRenderer.resetProjectionMatrix(event.getProjectionMatrix());

        Item held = player.getMainHandItem().getItem();
        if (player.getOffhandItem().getItem() != ModItems.ANGEL_WAND.get() || !(held instanceof BlockItem)) {
            return;
        }

        BlockHitResult blockHitResult = Util.getPlayerPOVHitResult(player);
        BlockPos pos = Util.getAdvancedPositionFromBlockHitResult(blockHitResult, player);
        if (pos == null) {
            return;
        }

        Block block = ((BlockItem) held).getBlock();
        if (!(block instanceof DoorBlock) && !(block instanceof BedBlock)
                && player.level.getBlockState(pos).getBlock() != block) {
            BlockState newState = Util.getPlacingBlockStateFromPlayer(player, block);
            if (!newState.canSurvive(player.level, pos)) {
                return;
            }

            PoseStack poseStack = event.getPoseStack();
            VoxelShape voxelShape = newState.getShape(player.level, pos);

            AngelWandBlockPlacingMarker.drawShapeOutline(poseStack, voxelShape, pos);
        }
    }

    private static void drawShapeOutline(PoseStack poseStack, VoxelShape voxelShape, BlockPos pos) {
        poseStack.pushPose();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        float red = 0.0f, green = 0.5f, blue = 0.5f, alpha = 0.5f;

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().reverse();
        double originX = cam.x + pos.getX(), originY = cam.y + pos.getY(), originZ = cam.z + pos.getZ();
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        MultiBufferSource.BufferSource renderTypeBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer bufferIn = renderTypeBuffer.getBuffer(RenderType.lines());

        voxelShape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            bufferIn.vertex(matrix4f, (float) (x0 + originX), (float) (y0 + originY), (float) (z0 + originZ))
                    .color(red, green, blue, alpha).normal(matrix3f, 1.0F, 1.0F, 1.0F).endVertex();
            bufferIn.vertex(matrix4f, (float) (x1 + originX), (float) (y1 + originY), (float) (z1 + originZ))
                    .color(red, green, blue, alpha).normal(matrix3f, 0.0F, 0.0F, 0.0F).endVertex();
        });

        renderTypeBuffer.endBatch(RenderType.lines());
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        poseStack.popPose();
    }
}