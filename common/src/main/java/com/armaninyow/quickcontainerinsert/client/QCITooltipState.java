package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QCITooltipState {

	private static final int SOLID_TICKS = 30;
	private static final int FADE_TICKS = 10;
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

	public static int getAlpha() {
		if (ticksRemaining <= 0) return 0;
		if (ticksRemaining > FADE_TICKS) return 255;
		return (int) (255f * ticksRemaining / FADE_TICKS);
	}

	public static void tick() {
		if (ticksRemaining > 0) ticksRemaining--;
		if (ticksRemaining == 0) type = TooltipType.NONE;
	}
}