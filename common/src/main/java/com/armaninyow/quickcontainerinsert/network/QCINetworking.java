package com.armaninyow.quickcontainerinsert.network;

import com.armaninyow.quickcontainerinsert.QuickContainerInsert;
import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCITooltipState;
import com.armaninyow.quickcontainerinsert.util.QCIServerInsertFlag;
import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;

public class QCINetworking {

	// ── Payloads ──────────────────────────────────────────────────────────────

	public record InsertRequestPayload(BlockPos pos, Direction face) implements CustomPacketPayload {
		public static final Type<InsertRequestPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(QuickContainerInsert.MOD_ID, "insert_request"));
		public static final StreamCodec<FriendlyByteBuf, InsertRequestPayload> CODEC = StreamCodec.of(
			(buf, p) -> { buf.writeBlockPos(p.pos()); buf.writeEnum(p.face()); },
			buf -> new InsertRequestPayload(buf.readBlockPos(), buf.readEnum(Direction.class))
		);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	public record EntityInsertRequestPayload(int entityId) implements CustomPacketPayload {
		public static final Type<EntityInsertRequestPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(QuickContainerInsert.MOD_ID, "entity_insert_request"));
		public static final StreamCodec<FriendlyByteBuf, EntityInsertRequestPayload> CODEC = StreamCodec.of(
			(buf, p) -> buf.writeVarInt(p.entityId()),
			buf -> new EntityInsertRequestPayload(buf.readVarInt())
		);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	// result: 0=success, 1=full, 2=not_allowed; entityId=-1 for block containers
	public record InsertResultPayload(BlockPos pos, int entityId, int result, String containerName) implements CustomPacketPayload {
		public static final Type<InsertResultPayload> TYPE =
			new Type<>(Identifier.fromNamespaceAndPath(QuickContainerInsert.MOD_ID, "insert_result"));
		public static final StreamCodec<FriendlyByteBuf, InsertResultPayload> CODEC = StreamCodec.of(
			(buf, p) -> { buf.writeBlockPos(p.pos()); buf.writeVarInt(p.entityId()); buf.writeVarInt(p.result()); buf.writeUtf(p.containerName()); },
			buf -> new InsertResultPayload(buf.readBlockPos(), buf.readVarInt(), buf.readVarInt(), buf.readUtf())
		);
		@Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
	}

	// ── Registration ─────────────────────────────────────────────────────────

	public static void registerServerPackets() {
		PayloadTypeRegistry.serverboundPlay().register(InsertRequestPayload.TYPE, InsertRequestPayload.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(EntityInsertRequestPayload.TYPE, EntityInsertRequestPayload.CODEC);
		PayloadTypeRegistry.clientboundPlay().register(InsertResultPayload.TYPE, InsertResultPayload.CODEC);

		// ── Block container handler ───────────────────────────────────────────
		ServerPlayNetworking.registerGlobalReceiver(InsertRequestPayload.TYPE, (payload, context) -> {
			BlockPos pos = payload.pos();
			Direction face = payload.face();
			ServerPlayer player = context.player();

			context.server().execute(() -> {
				// Set flag so handleUseItemOn suppresses the vanilla GUI open
				QCIServerInsertFlag.setInsertingBlock(player);
				try {
					Level level = player.level();
					if (!level.isLoaded(pos)) return;
					if (player.blockPosition().distSqr(pos) > 64) return;

					BlockEntity be = level.getBlockEntity(pos);
					String containerName = getContainerName(be, level, pos);
					ItemStack held = player.getMainHandItem();
					if (held.isEmpty()) return;

					ItemStack toInsert = held.copyWithCount(1);

					if (be instanceof EnderChestBlockEntity) {
						SimpleContainer ec = player.getEnderChestInventory();
						if (!ContainerUtil.isItemAllowed(ec, toInsert)) {
							broadcastResult(level, pos, -1, 2, containerName);
							return;
						}
						ItemStack remainder = ContainerUtil.insertItem(ec, toInsert);
						if (remainder.isEmpty()) {
							held.shrink(1);
							player.setItemInHand(InteractionHand.MAIN_HAND, held);
							broadcastResult(level, pos, -1, 0, containerName);
							playSound(level, pos, true);
						} else {
							broadcastResult(level, pos, -1, 1, containerName);
							playSound(level, pos, false);
						}
						return;
					}

					Container container = ContainerUtil.getContainer(be, face);
					if (container == null) return;

					if (be instanceof BrewingStandBlockEntity brewing) {
						int slot = ContainerUtil.getBrewingStandTargetSlot(brewing, toInsert);
						if (slot == -1) { broadcastResult(level, pos, -1, 2, containerName); return; }
						ItemStack remainder = ContainerUtil.insertItemIntoSlot(container, toInsert, slot);
						if (remainder.isEmpty()) {
							held.shrink(1); player.setItemInHand(InteractionHand.MAIN_HAND, held);
							broadcastResult(level, pos, -1, 0, containerName); playSound(level, pos, true);
						} else { broadcastResult(level, pos, -1, 1, containerName); playSound(level, pos, false); }
						return;
					}

					if (be instanceof AbstractFurnaceBlockEntity furnace) {
						int slot = ContainerUtil.getFurnaceTargetSlot(furnace, toInsert);
						if (slot == -1) { broadcastResult(level, pos, -1, 2, containerName); return; }
						ItemStack remainder = ContainerUtil.insertItemIntoSlot(container, toInsert, slot);
						if (remainder.isEmpty()) {
							held.shrink(1); player.setItemInHand(InteractionHand.MAIN_HAND, held);
							broadcastResult(level, pos, -1, 0, containerName); playSound(level, pos, true);
						} else { broadcastResult(level, pos, -1, 1, containerName); playSound(level, pos, false); }
						return;
					}

					if (!ContainerUtil.isItemAllowed(container, toInsert)) {
						broadcastResult(level, pos, -1, 2, containerName); return;
					}
					ItemStack remainder = ContainerUtil.insertItem(container, toInsert);
					if (remainder.isEmpty()) {
						held.shrink(1); player.setItemInHand(InteractionHand.MAIN_HAND, held);
						broadcastResult(level, pos, -1, 0, containerName);
						playSound(level, pos, true, computeFillRatio(container));
					} else { broadcastResult(level, pos, -1, 1, containerName); playSound(level, pos, false); }
				} finally {
					QCIServerInsertFlag.clearInsertingBlock(player);
				}
			});
		});

		// ── Entity container handler ──────────────────────────────────────────
		ServerPlayNetworking.registerGlobalReceiver(EntityInsertRequestPayload.TYPE, (payload, context) -> {
			int entityId = payload.entityId();
			ServerPlayer player = context.player();

			context.server().execute(() -> {
				QCIServerInsertFlag.setInsertingEntity(player);
				try {
					Level level = player.level();
					Entity entity = ((ServerLevel) level).getEntity(entityId);
					if (entity == null) return;
					if (!(entity instanceof ContainerEntity containerEntity)) return;
					if (player.distanceToSqr(entity) > 64) return;

					String containerName = entity.getName().getString();
					ItemStack held = player.getMainHandItem();
					if (held.isEmpty()) return;

					ItemStack toInsert = held.copyWithCount(1);
					Container container = (Container) containerEntity;

					if (!ContainerUtil.isItemAllowed(container, toInsert)) {
						broadcastEntityResult(level, entityId, 2, containerName); return;
					}
					ItemStack remainder = ContainerUtil.insertItem(container, toInsert);
					if (remainder.isEmpty()) {
						held.shrink(1); player.setItemInHand(InteractionHand.MAIN_HAND, held);
						broadcastEntityResult(level, entityId, 0, containerName);
						playSound(level, entity.blockPosition(), true, computeFillRatio(container));
					} else {
						broadcastEntityResult(level, entityId, 1, containerName);
						playSound(level, entity.blockPosition(), false);
					}
				} finally {
					QCIServerInsertFlag.clearInsertingEntity(player);
				}
			});
		});
	}

	@Environment(EnvType.CLIENT)
	public static void registerClientPackets() {
		ClientPlayNetworking.registerGlobalReceiver(InsertResultPayload.TYPE, (payload, context) -> {
			BlockPos pos = payload.pos();
			int entityId = payload.entityId();
			int result = payload.result();
			String name = payload.containerName();

			context.client().execute(() -> {
				QCIAnimationState.AnimationType animType = (result == 0)
					? QCIAnimationState.AnimationType.SUCCESS
					: QCIAnimationState.AnimationType.FAIL;

				if (entityId == -1) {
					triggerWithPairedChest(pos, animType, context.client().level);
				} else {
					QCIAnimationState.triggerEntity(entityId, animType);
				}

				QCIAnimationTicker.register();

				if (result == 0) {
					com.armaninyow.quickcontainerinsert.client.QCIClientHandler.onInsertSuccess();
				} else if (result == 1) {
					com.armaninyow.quickcontainerinsert.client.QCIClientHandler.onInsertFailure();
					QCITooltipState.triggerFull(name);
				} else {
					com.armaninyow.quickcontainerinsert.client.QCIClientHandler.onInsertFailure();
					QCITooltipState.triggerNotAllowed(name);
					net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
					if (mc.player != null && mc.level != null) {
						BlockPos soundPos = (entityId == -1) ? pos : mc.player.blockPosition();
						mc.level.playLocalSound(soundPos, SoundEvents.DECORATED_POT_INSERT_FAIL,
							SoundSource.BLOCKS, 0.9f, 1.0f, false);
					}
				}
			});
		});
	}

	@Environment(EnvType.CLIENT)
	private static void triggerWithPairedChest(
		BlockPos pos, QCIAnimationState.AnimationType type,
		net.minecraft.world.level.Level level
	) {
		if (level == null) { QCIAnimationState.trigger(pos, type); return; }

		net.minecraft.world.level.block.state.BlockState state = level.getBlockState(pos);
		net.minecraft.world.level.block.Block block = state.getBlock();

		if (!(block instanceof net.minecraft.world.level.block.ChestBlock)
			|| !state.hasProperty(net.minecraft.world.level.block.ChestBlock.TYPE)) {
			QCIAnimationState.trigger(pos, type); return;
		}

		net.minecraft.world.level.block.state.properties.ChestType chestType =
			state.getValue(net.minecraft.world.level.block.ChestBlock.TYPE);
		if (chestType == net.minecraft.world.level.block.state.properties.ChestType.SINGLE) {
			QCIAnimationState.trigger(pos, type); return;
		}

		net.minecraft.core.Direction toOther =
			net.minecraft.world.level.block.ChestBlock.getConnectedDirection(state);
		BlockPos pairedPos = pos.relative(toOther);

		float clickedPX = 0.5f + toOther.getStepX() * 0.5f;
		float clickedPZ = 0.5f + toOther.getStepZ() * 0.5f;
		float pairedPX  = 0.5f - toOther.getStepX() * 0.5f;
		float pairedPZ  = 0.5f - toOther.getStepZ() * 0.5f;

		QCIAnimationState.trigger(pos,       type, clickedPX, 0.0f, clickedPZ);
		QCIAnimationState.trigger(pairedPos, type, pairedPX,  0.0f, pairedPZ);
	}

	@Environment(EnvType.CLIENT)
	public static void sendInsertRequest(BlockPos pos, Direction face) {
		ClientPlayNetworking.send(new InsertRequestPayload(pos, face));
	}

	@Environment(EnvType.CLIENT)
	public static void sendEntityInsertRequest(int entityId) {
		ClientPlayNetworking.send(new EntityInsertRequestPayload(entityId));
	}

	private static void broadcastResult(Level level, BlockPos pos, int entityId, int result, String containerName) {
		InsertResultPayload payload = new InsertResultPayload(pos, entityId, result, containerName);
		for (ServerPlayer p : ((ServerLevel) level).players()) {
			if (p.blockPosition().distSqr(pos) <= 4096) ServerPlayNetworking.send(p, payload);
		}
	}

	private static void broadcastEntityResult(Level level, int entityId, int result, String containerName) {
		InsertResultPayload payload = new InsertResultPayload(BlockPos.ZERO, entityId, result, containerName);
		for (ServerPlayer p : ((ServerLevel) level).players()) {
			ServerPlayNetworking.send(p, payload);
		}
	}

	private static void playSound(Level level, BlockPos pos, boolean success) {
		playSound(level, pos, success, 0.5f);
	}

	private static void playSound(Level level, BlockPos pos, boolean success, float fillRatio) {
		if (success) {
			float pitch = 0.7f + 0.5f * fillRatio;
			level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0f, pitch);
		} else {
			level.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT_FAIL, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
	}

	private static String getContainerName(BlockEntity be, Level level, BlockPos pos) {
		return level.getBlockState(pos).getBlock().getName().getString();
	}

	private static float computeFillRatio(Container container) {
		int filled = 0, total = 0;
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack s = container.getItem(i);
			total += s.getMaxStackSize();
			filled += s.getCount();
		}
		return total == 0 ? 0f : (float) filled / total;
	}
}