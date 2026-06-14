package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QCITooltipState {

	// 2 seconds total: 1.5s solid + 0.5s fade = 40 ticks
	private static final int SOLID_TICKS = 30; // 1.5s
	private static final int FADE_TICKS = 10;  // 0.5s
	public static final int TOTAL_TICKS = SOLID_TICKS + FADE_TICKS;

	public enum TooltipType {
		NONE, CONTAINER_FULL, ITEM_NOT_ALLOWED
	}

	private static int ticksRemaining = 0;
	private static TooltipType type = TooltipType.NONE;
	private static String containerName = "";

	public static void triggerFull(String name) {
		ticksRemaining = TOTAL_TICKS;
		type = TooltipType.CONTAINER_FULL;
		containerName = name;
	}

	public static void triggerNotAllowed(String name) {
		ticksRemaining = TOTAL_TICKS;
		type = TooltipType.ITEM_NOT_ALLOWED;
		containerName = name;
	}

	public static boolean isActive() {
		return ticksRemaining > 0 && type != TooltipType.NONE;
	}

	public static TooltipType getType() {
		return type;
	}

	public static String getContainerName() {
		return containerName;
	}

	/**
	 * Returns alpha 0-255: fully opaque during solid phase, fading during fade phase.
	 */
	public static int getAlpha() {
		if (ticksRemaining <= 0) return 0;
		if (ticksRemaining > FADE_TICKS) return 255;
		// Fade out: ticksRemaining goes from FADE_TICKS down to 1
		return (int) (255f * ticksRemaining / FADE_TICKS);
	}

	public static void tick() {
		if (ticksRemaining > 0) ticksRemaining--;
		if (ticksRemaining == 0) type = TooltipType.NONE;
	}
}