package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCIRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ChestRenderer.class)
public class ChestRendererMixin<T extends BlockEntity> {

	@Inject(
		method = "render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
		at = @At("HEAD")
	)
	private void qci$injectWobble(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, CallbackInfo ci) {
		if (!(blockEntity instanceof ChestBlockEntity chest)) return;
		BlockPos pos = chest.getBlockPos();
		QCIRenderUtil.applyWobble(poseStack, pos, partialTick);
	}
}