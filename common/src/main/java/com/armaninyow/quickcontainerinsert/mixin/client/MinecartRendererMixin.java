package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCIEntityRenderStateTracker;
import com.armaninyow.quickcontainerinsert.client.QCIRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractMinecartRenderer.class)
public class MinecartRendererMixin {

	@Inject(method = "submitMinecartContents", at = @At("HEAD"))
	private void qci$injectWobble(MinecartRenderState renderState, BlockModelRenderState blockModel,
	                               PoseStack poseStack, SubmitNodeCollector submitNodeCollector,
	                               int lightCoords, CallbackInfo ci) {
		Integer entityId = QCIEntityRenderStateTracker.getEntityId(renderState);
		if (entityId == null) return;
		if (QCIAnimationState.getEntityEntry(entityId) == null) return;
		float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
		QCIRenderUtil.applyWobbleEntityAtPivot(poseStack, entityId, partialTick, 0.5f, 0.5f, 0.5f);
	}
}