package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class QCIAnimationState {

	public static final int SUCCESS_DURATION = 7;
	public static final int FAIL_DURATION = 10;

	public enum AnimationType {
		SUCCESS, FAIL
	}

	public static final class AnimEntry {
		public final AnimationType type;
		public long startTick;
		public final int totalDuration;
		public final float pivotX;
		public final float pivotY;
		public final float pivotZ;

		public AnimEntry(AnimationType type, long startTick) {
			this(type, startTick, 0.5f, 0.0f, 0.5f);
		}

		public AnimEntry(AnimationType type, long startTick,
		                 float pivotX, float pivotY, float pivotZ) {
			this.type = type;
			this.startTick = startTick;
			this.totalDuration = (type == AnimationType.SUCCESS) ? SUCCESS_DURATION : FAIL_DURATION;
			this.pivotX = pivotX;
			this.pivotY = pivotY;
			this.pivotZ = pivotZ;
		}
	}

	// Block-based animations keyed by BlockPos
	private static final Map<BlockPos, AnimEntry> activeAnimations = new HashMap<>();
	// Entity-based animations keyed by entity ID
	private static final Map<Integer, AnimEntry> entityAnimations = new HashMap<>();

	private static long currentTick = 0;

	public static void trigger(BlockPos pos, AnimationType type) {
		activeAnimations.put(pos, new AnimEntry(type, currentTick));
	}

	public static void trigger(BlockPos pos, AnimationType type,
	                           float pivotX, float pivotY, float pivotZ) {
		activeAnimations.put(pos, new AnimEntry(type, currentTick, pivotX, pivotY, pivotZ));
	}

	public static void triggerEntity(int entityId, AnimationType type) {
		entityAnimations.put(entityId, new AnimEntry(type, currentTick, 0.0f, 0.0f, 0.0f));
	}

	public static AnimEntry getEntry(BlockPos pos) {
		return activeAnimations.get(pos);
	}

	public static AnimEntry getEntityEntry(int entityId) {
		return entityAnimations.get(entityId);
	}

	public static void tick() {
		currentTick++;
		Iterator<Map.Entry<BlockPos, AnimEntry>> it = activeAnimations.entrySet().iterator();
		while (it.hasNext()) {
			AnimEntry e = it.next().getValue();
			if (currentTick - e.startTick > e.totalDuration) it.remove();
		}
		Iterator<Map.Entry<Integer, AnimEntry>> it2 = entityAnimations.entrySet().iterator();
		while (it2.hasNext()) {
			AnimEntry e = it2.next().getValue();
			if (currentTick - e.startTick > e.totalDuration) it2.remove();
		}
	}

	private static float getProgress(AnimEntry entry, float partialTick) {
		float elapsed = (currentTick - entry.startTick) + partialTick;
		return elapsed / entry.totalDuration;
	}

	public static float getWobbleX(BlockPos pos, float partialTick) {
		return getWobbleXFromEntry(activeAnimations.get(pos), partialTick);
	}

	public static float getWobbleXEntity(int entityId, float partialTick) {
		return getWobbleXFromEntry(entityAnimations.get(entityId), partialTick);
	}

	private static float getWobbleXFromEntry(AnimEntry entry, float partialTick) {
		if (entry == null || entry.type != AnimationType.SUCCESS) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float k = g * (float) (Math.PI * 2);
		float l = -1.5f * (Mth.cos(k) + 0.5f) * Mth.sin(k / 2.0f);
		return l * 0.015625f;
	}

	public static float getWobbleZ(BlockPos pos, float partialTick) {
		return getWobbleZFromEntry(activeAnimations.get(pos), partialTick);
	}

	public static float getWobbleZEntity(int entityId, float partialTick) {
		return getWobbleZFromEntry(entityAnimations.get(entityId), partialTick);
	}

	private static float getWobbleZFromEntry(AnimEntry entry, float partialTick) {
		if (entry == null || entry.type != AnimationType.SUCCESS) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float k = g * (float) (Math.PI * 2);
		float m = Mth.sin(k);
		return m * 0.015625f;
	}

	public static float getWobbleY(BlockPos pos, float partialTick) {
		return getWobbleYFromEntry(activeAnimations.get(pos), partialTick);
	}

	public static float getWobbleYEntity(int entityId, float partialTick) {
		return getWobbleYFromEntry(entityAnimations.get(entityId), partialTick);
	}

	private static float getWobbleYFromEntry(AnimEntry entry, float partialTick) {
		if (entry == null || entry.type != AnimationType.FAIL) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float h = Mth.sin(-g * 3.0f * (float) Math.PI) * 0.125f;
		float k = 1.0f - g;
		return h * k;
	}
}