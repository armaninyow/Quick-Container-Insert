package com.armaninyow.quickcontainerinsert.client;

import com.armaninyow.quickcontainerinsert.config.QCIConfig;
import com.armaninyow.quickcontainerinsert.network.QCIAnimationTicker;
import com.armaninyow.quickcontainerinsert.network.QCINetworking;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class QCIClientHandler {

	private static int holdTicks = 0;
	private static boolean wasHolding = false;
	private static int transferCooldown = 0;
	private static BlockPos lastPos = null;

	public static void register() {
		QCIAnimationTicker.register();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if (client.player == null || client.level == null) {
				reset();
				return;
			}

			Player player = client.player;
			boolean ctrlHeld = client.getWindow() != null &&
				org.lwjgl.glfw.GLFW.glfwGetKey(
					client.getWindow().getWindow(),
					org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL
				) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
			boolean useHeld = client.options.keyUse.isDown();
			boolean holding = ctrlHeld && useHeld;

			if (!holding) {
				reset();
				return;
			}

			HitResult hit = client.hitResult;
			if (!(hit instanceof BlockHitResult blockHit)) {
				reset();
				return;
			}

			BlockPos pos = blockHit.getBlockPos();
			BlockState state = client.level.getBlockState(pos);

			if (!ContainerUtil.isContainerBlock(state.getBlock())) {
				reset();
				return;
			}

			ItemStack held = player.getMainHandItem();
			if (held.isEmpty()) {
				reset();
				return;
			}

			holdTicks++;

			int initialDelay = QCIConfig.getInitialDelay();
			int transferSpeed = QCIConfig.getTransferSpeed();

			if (!wasHolding) {
				wasHolding = true;
				lastPos = pos;
				sendInsert(player, pos, blockHit);
				transferCooldown = transferSpeed;
			} else if (holdTicks >= initialDelay) {
				transferCooldown--;
				if (transferCooldown <= 0) {
					sendInsert(player, pos, blockHit);
					transferCooldown = transferSpeed;
				}
			}
		});
	}

	private static void sendInsert(Player player, BlockPos pos, BlockHitResult blockHit) {
		QCINetworking.sendInsertRequest(pos, blockHit.getDirection());
		player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
	}

	/** Called by QCINetworking on insert success — arm swing already done on send. */
	public static void onInsertSuccess() {}

	private static void reset() {
		holdTicks = 0;
		wasHolding = false;
		transferCooldown = 0;
		lastPos = null;
	}
}