package com.armaninyow.quickcontainerinsert.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.armaninyow.quickcontainerinsert.config.QCIConfig;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public final class QCIRenderUtil {

	private QCIRenderUtil() {}

	/** For block containers — rotates around the given block-local pivot. */
	public static void applyWobble(PoseStack poseStack, BlockPos pos, float partialTick) {
		if (!QCIConfig.isAnimationEnabled()) return;
		QCIAnimationState.AnimEntry entry = QCIAnimationState.getEntry(pos);
		if (entry == null) return;

		float px = entry.pivotX;
		float py = entry.pivotY;
		float pz = entry.pivotZ;

		if (entry.type == QCIAnimationState.AnimationType.SUCCESS) {
			float rotX = QCIAnimationState.getWobbleX(pos, partialTick);
			float rotZ = QCIAnimationState.getWobbleZ(pos, partialTick);
			if (rotX == 0f && rotZ == 0f) return;
			poseStack.rotateAround(Axis.XP.rotation(rotX), px, py, pz);
			poseStack.rotateAround(Axis.ZP.rotation(rotZ), px, py, pz);
		} else {
			float rotY = QCIAnimationState.getWobbleY(pos, partialTick);
			if (rotY == 0f) return;
			poseStack.rotateAround(Axis.YP.rotation(rotY), px, py, pz);
		}
	}

	/** For entity containers (boats/rafts) — rotates around origin (model center). */
	public static void applyWobbleEntity(PoseStack poseStack, int entityId, float partialTick) {
		if (!QCIConfig.isAnimationEnabled()) return;
		QCIAnimationState.AnimEntry entry = QCIAnimationState.getEntityEntry(entityId);
		if (entry == null) return;

		if (entry.type == QCIAnimationState.AnimationType.SUCCESS) {
			float rotX = QCIAnimationState.getWobbleXEntity(entityId, partialTick);
			float rotZ = QCIAnimationState.getWobbleZEntity(entityId, partialTick);
			if (rotX == 0f && rotZ == 0f) return;
			poseStack.mulPose(Axis.XP.rotation(rotX));
			poseStack.mulPose(Axis.ZP.rotation(rotZ));
		} else {
			float rotY = QCIAnimationState.getWobbleYEntity(entityId, partialTick);
			if (rotY == 0f) return;
			poseStack.mulPose(Axis.YP.rotation(rotY));
		}
	}

	/** For minecart contents — rotates around a specific pivot in block-local space. */
	public static void applyWobbleEntityAtPivot(PoseStack poseStack, int entityId, float partialTick,
	                                             float px, float py, float pz) {
		if (!QCIConfig.isAnimationEnabled()) return;
		QCIAnimationState.AnimEntry entry = QCIAnimationState.getEntityEntry(entityId);
		if (entry == null) return;

		if (entry.type == QCIAnimationState.AnimationType.SUCCESS) {
			float rotX = QCIAnimationState.getWobbleXEntity(entityId, partialTick);
			float rotZ = QCIAnimationState.getWobbleZEntity(entityId, partialTick);
			if (rotX == 0f && rotZ == 0f) return;
			poseStack.rotateAround(Axis.XP.rotation(rotX), px, py, pz);
			poseStack.rotateAround(Axis.ZP.rotation(rotZ), px, py, pz);
		} else {
			float rotY = QCIAnimationState.getWobbleYEntity(entityId, partialTick);
			if (rotY == 0f) return;
			poseStack.rotateAround(Axis.YP.rotation(rotY), px, py, pz);
		}
	}
}