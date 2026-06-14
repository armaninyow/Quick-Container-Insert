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

	/**
	 * Applies the exact decorated pot wobble transform to the PoseStack.
	 *
	 * Mirrors DecoratedPotRenderer.render() exactly:
	 * - POSITIVE (success): rotateAround X and Z at pivot
	 * - NEGATIVE (fail): rotateAround Y at pivot
	 *
	 * The pivot is stored in AnimEntry and defaults to (0.5, 0, 0.5) for
	 * single blocks. For large chest halves the pivot is shifted to the
	 * shared midpoint of the two-block footprint so both halves rotate as one.
	 */
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
}