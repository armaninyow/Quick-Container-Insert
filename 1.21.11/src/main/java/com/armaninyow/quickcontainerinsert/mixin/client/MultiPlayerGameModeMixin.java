package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

	@Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
	private void qci$cancelPlacementOnContainer(
		LocalPlayer player, InteractionHand hand,
		BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir
	) {
		long handle = net.minecraft.client.Minecraft.getInstance().getWindow().handle();
		if (org.lwjgl.glfw.GLFW.glfwGetKey(handle, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL)
				!= org.lwjgl.glfw.GLFW.GLFW_PRESS) return;
		if (hand != InteractionHand.MAIN_HAND) return;

		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) return;

		BlockPos pos = hitResult.getBlockPos();
		if (!player.level().isLoaded(pos)) return;

		if (!ContainerUtil.isContainerBlock(player.level().getBlockState(pos).getBlock())) return;

		cir.setReturnValue(InteractionResult.FAIL);
	}
}