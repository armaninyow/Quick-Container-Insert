package com.armaninyow.quickcontainerinsert.client;

import com.armaninyow.quickcontainerinsert.config.QCIConfig;
import com.armaninyow.quickcontainerinsert.network.QCIAnimationTicker;
import com.armaninyow.quickcontainerinsert.network.QCINetworking;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class QCIClientHandler {

	private static int holdTicks = 0;
	private static boolean wasHolding = false;
	private static int transferCooldown = 0;
	private static BlockPos lastPos = null;
	private static boolean lastWasEntity = false;

	public static void register() {
		QCIAnimationTicker.register();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {

			if (client.player == null || client.level == null) {
				reset();
				return;
			}

			Player player = client.player;
			boolean ctrlHeld = QCIKeys.isCtrlDown();
			boolean useHeld = client.options.keyUse.isDown();
			boolean holding = ctrlHeld && useHeld;

			if (!holding) {
				reset();
				return;
			}

			HitResult hit = client.hitResult;
			ItemStack held = player.getMainHandItem();
			if (held.isEmpty() && !QCIInsertState.isInserting()) {
				reset();
				return;
			}

			if (hit instanceof EntityHitResult entityHit) {
				Entity entity = entityHit.getEntity();
				if (!(entity instanceof ContainerEntity)) {
					reset();
					return;
				}

				holdTicks++;
				int initialDelay = QCIConfig.getInitialDelay();
				int transferSpeed = QCIConfig.getTransferSpeed();

				if (!wasHolding) {
					wasHolding = true;
					lastPos = null;
					sendEntityInsert(entity.getId());
					transferCooldown = transferSpeed;
				} else if (holdTicks >= initialDelay) {
					transferCooldown--;
					if (transferCooldown <= 0) {
						sendEntityInsert(entity.getId());
						transferCooldown = transferSpeed;
					}
				}
				return;
			}

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

			holdTicks++;
			int initialDelay = QCIConfig.getInitialDelay();
			int transferSpeed = QCIConfig.getTransferSpeed();

			if (!wasHolding) {
				wasHolding = true;
				lastPos = pos;
				sendInsert(pos, blockHit);
				transferCooldown = transferSpeed;
			} else if (holdTicks >= initialDelay) {
				transferCooldown--;
				if (transferCooldown <= 0) {
					sendInsert(pos, blockHit);
					transferCooldown = transferSpeed;
				}
			}
		});
	}

	private static void sendInsert(BlockPos pos, BlockHitResult blockHit) {
		lastWasEntity = false;
		QCIInsertState.setInserting(true);
		QCINetworking.sendInsertRequest(pos, blockHit.getDirection());
	}

	private static void sendEntityInsert(int entityId) {
		lastWasEntity = true;
		QCIInsertState.setInserting(true);
		QCINetworking.sendEntityInsertRequest(entityId);
	}

	public static void onInsertSuccess() {
		QCIInsertState.setInserting(false);
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null) {
			mc.player.swing(InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT, false);
		}
	}

	public static void onInsertFailure() {
		QCIInsertState.setInserting(false);
	}

	private static void reset() {
		holdTicks = 0;
		wasHolding = false;
		transferCooldown = 0;
		lastPos = null;
		lastWasEntity = false;
	}
}