package com.armaninyow.quickcontainerinsert.network;

import com.armaninyow.quickcontainerinsert.client.QCIAnimationState;
import com.armaninyow.quickcontainerinsert.client.QCITooltipState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class QCIAnimationTicker {

	private static boolean registered = false;

	public static void register() {
		if (registered) return;
		registered = true;
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			QCIAnimationState.tick();
			QCITooltipState.tick();
		});
	}
}