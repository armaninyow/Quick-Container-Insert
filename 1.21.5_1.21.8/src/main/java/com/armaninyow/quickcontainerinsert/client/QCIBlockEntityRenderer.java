package com.armaninyow.quickcontainerinsert.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@Environment(EnvType.CLIENT)
public class QCIBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {

	private final BlockRenderDispatcher blockRenderer;
	private final RandomSource random = RandomSource.create();

	public QCIBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		this.blockRenderer = ctx.getBlockRenderDispatcher();
	}

	@Override
	public void render(T blockEntity, float partialTick, PoseStack poseStack,
	                   MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, Vec3 cameraPos) {
		Level level = blockEntity.getLevel();
		if (level == null) return;

		BlockPos pos = blockEntity.getBlockPos();
		BlockState state = blockEntity.getBlockState();

		QCISingleBlockView view = new QCISingleBlockView(level, pos, state);

		poseStack.pushPose();

		if (QCIAnimationState.getEntry(pos) != null) {
			QCIRenderUtil.applyWobble(poseStack, pos, partialTick);
		}

		RenderType renderType = ItemBlockRenderTypes.getRenderType(state);
		List<BlockModelPart> parts = blockRenderer.getBlockModel(state).collectParts(random);

		QCIRenderSuppression.SKIP_SUPPRESSION.set(true);
		try {
			this.blockRenderer.renderBatched(state, pos, view, poseStack,
				bufferSource.getBuffer(renderType), false, parts);
		} finally {
			QCIRenderSuppression.SKIP_SUPPRESSION.set(false);
		}

		poseStack.popPose();
	}
}