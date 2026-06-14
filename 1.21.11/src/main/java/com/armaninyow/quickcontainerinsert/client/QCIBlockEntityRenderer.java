package com.armaninyow.quickcontainerinsert.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;

@Environment(EnvType.CLIENT)
public class QCIBlockEntityRenderer<T extends BlockEntity>
		implements BlockEntityRenderer<T, QCIBlockRenderState> {

	public QCIBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
	}

	@Override
	public QCIBlockRenderState createRenderState() {
		return new QCIBlockRenderState();
	}

	@Override
	public void extractRenderState(T blockEntity, QCIBlockRenderState renderState, float partialTick,
	                               Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderState.extractBase(blockEntity, renderState, crumblingOverlay);
		renderState.level = blockEntity.getLevel();
	}

	@Override
	public void submit(QCIBlockRenderState renderState, PoseStack poseStack,
	                   SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		BlockPos pos = renderState.blockPos;
		BlockState state = renderState.blockState;
		Level level = renderState.level;
		if (state == null || pos == null || level == null) return;

		MovingBlockRenderState movingState = new MovingBlockRenderState();
		movingState.blockPos = pos;
		movingState.randomSeedPos = pos;
		movingState.blockState = state;
		movingState.level = level;
		movingState.biome = level.getBiome(pos);

		poseStack.pushPose();

		if (QCIAnimationState.getEntry(pos) != null) {
			QCIRenderUtil.applyWobble(poseStack, pos,
				net.minecraft.client.Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true));
		}

		submitNodeCollector.submitMovingBlock(poseStack, movingState);

		poseStack.popPose();
	}
}