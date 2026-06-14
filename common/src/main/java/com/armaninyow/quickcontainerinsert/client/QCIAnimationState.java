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

	// Exact durations from DecoratedPotBlockEntity.WobbleStyle
	public static final int SUCCESS_DURATION = 7;  // POSITIVE
	public static final int FAIL_DURATION = 10;    // NEGATIVE

	public enum AnimationType {
		SUCCESS, FAIL
	}

	public static final class AnimEntry {
		public final AnimationType type;
		public long startTick;
		public final int totalDuration;

		/**
		 * The pivot point (in block-local space) around which this block's
		 * wobble should rotate. For single blocks and non-chest containers this
		 * is always (0.5, 0, 0.5) — the block's own centre.
		 *
		 * For large chests both halves must rotate around the shared midpoint
		 * of the two-block footprint, so the pivot X or Z component is shifted
		 * by ±0.5 depending on which half this is and the connection direction.
		 */
		public final float pivotX;
		public final float pivotY;
		public final float pivotZ;

		/** Constructs an entry with the default single-block pivot (0.5, 0, 0.5). */
		public AnimEntry(AnimationType type, long startTick) {
			this(type, startTick, 0.5f, 0.0f, 0.5f);
		}

		/** Constructs an entry with a custom pivot for large-chest halves. */
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

	private static final Map<BlockPos, AnimEntry> activeAnimations = new HashMap<>();
	private static long currentTick = 0;

	/** Trigger with default pivot (single block or non-chest). */
	public static void trigger(BlockPos pos, AnimationType type) {
		activeAnimations.put(pos, new AnimEntry(type, currentTick));
	}

	/**
	 * Trigger with an explicit pivot offset (block-local space).
	 * Used for large chest halves so both share the same world-space pivot.
	 */
	public static void trigger(BlockPos pos, AnimationType type,
	                           float pivotX, float pivotY, float pivotZ) {
		activeAnimations.put(pos, new AnimEntry(type, currentTick, pivotX, pivotY, pivotZ));
	}

	public static AnimEntry getEntry(BlockPos pos) {
		return activeAnimations.get(pos);
	}

	public static void tick() {
		currentTick++;
		Iterator<Map.Entry<BlockPos, AnimEntry>> it = activeAnimations.entrySet().iterator();
		while (it.hasNext()) {
			AnimEntry e = it.next().getValue();
			long elapsed = currentTick - e.startTick;
			if (elapsed > e.totalDuration) {
				it.remove();
			}
		}
	}

	/**
	 * Returns the progress g in [0, 1] matching DecoratedPotRenderer's g calculation:
	 * g = (gameTime - wobbleStartedAtTick + partialTick) / duration
	 */
	private static float getProgress(AnimEntry entry, float partialTick) {
		float elapsed = (currentTick - entry.startTick) + partialTick;
		return elapsed / entry.totalDuration;
	}

	/**
	 * Returns X-axis rotation in radians — exact copy of POSITIVE wobble X component.
	 * l = -1.5 * (cos(g*2π) + 0.5) * sin(g*π)
	 * applied as: Axis.XP.rotation(l * 0.015625F) around pivot
	 */
	public static float getWobbleX(BlockPos pos, float partialTick) {
		AnimEntry entry = activeAnimations.get(pos);
		if (entry == null || entry.type != AnimationType.SUCCESS) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float k = g * (float) (Math.PI * 2);
		float l = -1.5f * (Mth.cos(k) + 0.5f) * Mth.sin(k / 2.0f);
		return l * 0.015625f;
	}

	/**
	 * Returns Z-axis rotation in radians — exact copy of POSITIVE wobble Z component.
	 * m = sin(g*2π)
	 * applied as: Axis.ZP.rotation(m * 0.015625F) around pivot
	 */
	public static float getWobbleZ(BlockPos pos, float partialTick) {
		AnimEntry entry = activeAnimations.get(pos);
		if (entry == null || entry.type != AnimationType.SUCCESS) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float k = g * (float) (Math.PI * 2);
		float m = Mth.sin(k);
		return m * 0.015625f;
	}

	/**
	 * Returns Y-axis rotation in radians — exact copy of NEGATIVE wobble.
	 * h = sin(-g * 3π) * 0.125 * (1 - g)
	 * applied as: Axis.YP.rotation(h) around pivot
	 */
	public static float getWobbleY(BlockPos pos, float partialTick) {
		AnimEntry entry = activeAnimations.get(pos);
		if (entry == null || entry.type != AnimationType.FAIL) return 0f;
		float g = getProgress(entry, partialTick);
		if (g < 0f || g > 1f) return 0f;
		float h = Mth.sin(-g * 3.0f * (float) Math.PI) * 0.125f;
		float k = 1.0f - g;
		return h * k;
	}
}