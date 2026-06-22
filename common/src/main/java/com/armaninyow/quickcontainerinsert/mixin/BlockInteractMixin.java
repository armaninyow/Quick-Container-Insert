package com.armaninyow.quickcontainerinsert.mixin;

import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import com.armaninyow.quickcontainerinsert.util.QCIServerInsertFlag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
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
		// Only suppress if the client already sent our custom insert packet
		if (!QCIServerInsertFlag.isInsertingBlock(player)) return;

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

	@Inject(method = "handleInteract", at = @At("HEAD"), cancellable = true)
	private void qci$suppressEntityGuiOnCtrlClick(ServerboundInteractPacket packet, CallbackInfo ci) {
		if (player == null) return;
		if (!QCIServerInsertFlag.isInsertingEntity(player)) return;
		if (packet.hand() != InteractionHand.MAIN_HAND) return;

		ItemStack held = player.getMainHandItem();
		if (held.isEmpty()) return;

		Entity entity = player.level().getEntity(packet.entityId());
		if (!(entity instanceof ContainerEntity)) return;

		ci.cancel();
	}
}