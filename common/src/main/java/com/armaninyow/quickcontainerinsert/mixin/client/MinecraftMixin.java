package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIInsertState;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class MinecraftMixin {

	@Shadow
	public HitResult hitResult;

	@Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
	private void qci$cancelOnCtrlUse(CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		if (mc.player == null || mc.level == null) return;

		long handle = mc.getWindow().handle();
		if (org.lwjgl.glfw.GLFW.glfwGetKey(handle, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL)
				!= org.lwjgl.glfw.GLFW.GLFW_PRESS) return;

		// Also cancel if we're still processing a previous insert (e.g. last item)
		if (QCIInsertState.isInserting()) {
			ci.cancel();
			return;
		}

		if (hitResult instanceof EntityHitResult entityHit) {
			Entity entity = entityHit.getEntity();
			if (entity instanceof ContainerEntity) ci.cancel();
			return;
		}

		if (hitResult instanceof BlockHitResult blockHit) {
			BlockPos pos = blockHit.getBlockPos();
			if (ContainerUtil.isContainerBlock(mc.level.getBlockState(pos).getBlock())) {
				ci.cancel();
			}
		}
	}
}