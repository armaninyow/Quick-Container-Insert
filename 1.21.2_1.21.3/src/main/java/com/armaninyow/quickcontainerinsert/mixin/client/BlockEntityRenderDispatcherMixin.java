package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCIRenderUtil;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

	@Inject(
		method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V",
		at = @At("HEAD")
	)
	private <T extends BlockEntity> void qci$applyWobble(
		T blockEntity, float partialTick, PoseStack poseStack,
		MultiBufferSource bufferSource, CallbackInfo ci
	) {
		if (blockEntity == null) return;
		BlockPos pos = blockEntity.getBlockPos();
		if (QCIAnimationState.getEntry(pos) == null) return;
		if (blockEntity.getLevel() == null) return;
		if (!ContainerUtil.isContainerBlock(blockEntity.getBlockState().getBlock())) return;
		QCIRenderUtil.applyWobble(poseStack, pos, partialTick);
	}
}