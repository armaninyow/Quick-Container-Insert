package com.armaninyow.quickcontainerinsert.config;

import com.armaninyow.quickcontainerinsert.QuickContainerInsert;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = QuickContainerInsert.MOD_ID)
public class QCIConfig implements ConfigData {

	@ConfigEntry.Gui.Tooltip
	public boolean enableAnimation = true;

	@ConfigEntry.BoundedDiscrete(min = 1, max = 20)
	@ConfigEntry.Gui.Tooltip
	public int transferSpeed = 4;

	@ConfigEntry.BoundedDiscrete(min = 1, max = 20)
	@ConfigEntry.Gui.Tooltip
	public int initialDelay = 10;

	// ── Static access helpers ─────────────────────────────────────────────────

	private static QCIConfig instance;

	public static void load() {
		AutoConfig.register(QCIConfig.class, GsonConfigSerializer::new);
		instance = AutoConfig.getConfigHolder(QCIConfig.class).getConfig();
	}

	public static boolean isAnimationEnabled() {
		return instance != null ? instance.enableAnimation : true;
	}

	public static int getTransferSpeed() {
		return instance != null ? instance.transferSpeed : 2;
	}

	public static int getInitialDelay() {
		return instance != null ? instance.initialDelay : 10;
	}
}