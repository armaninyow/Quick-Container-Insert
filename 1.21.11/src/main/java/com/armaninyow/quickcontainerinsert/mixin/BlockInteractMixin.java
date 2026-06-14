package com.armaninyow.quickcontainerinsert.mixin;

import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class BlockInteractMixin {

	@Shadow
	public ServerPlayer player;

	@Inject(method = "handleUseItemOn", at = @At("HEAD"), cancellable = true)
	private void qci$suppressGuiOnCtrlClick(ServerboundUseItemOnPacket packet, CallbackInfo ci) {
		if (player == null) return;
		long handle = net.minecraft.client.Minecraft.getInstance().getWindow().handle();
		if (org.lwjgl.glfw.GLFW.glfwGetKey(handle, org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL)
				!= org.lwjgl.glfw.GLFW.GLFW_PRESS) return;

		InteractionHand hand = packet.getHand();
		if (hand != InteractionHand.MAIN_HAND) return;

		ItemStack held = player.getItemInHand(hand);
		if (held.isEmpty()) return;

		BlockHitResult hitResult = packet.getHitResult();
		BlockPos pos = hitResult.getBlockPos();
		if (!player.level().isLoaded(pos)) return;

		BlockState state = player.level().getBlockState(pos);
		if (!ContainerUtil.isContainerBlock(state.getBlock())) return;

		ci.cancel();
	}
}