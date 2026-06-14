package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCIRenderUtil;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

	@Inject(
		method = "submit",
		at = @At("HEAD")
	)
	private <S extends BlockEntityRenderState> void qci$applyWobble(
		S renderState, PoseStack poseStack,
		SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState,
		CallbackInfo ci
	) {
		if (renderState == null) return;
		BlockPos pos = renderState.blockPos;
		if (pos == null) return;
		if (QCIAnimationState.getEntry(pos) == null) return;
		if (renderState.blockState == null) return;
		if (!ContainerUtil.isContainerBlock(renderState.blockState.getBlock())) return;
		QCIRenderUtil.applyWobble(poseStack, pos, 0f);
	}
}