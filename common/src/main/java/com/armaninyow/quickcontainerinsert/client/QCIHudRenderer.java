package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import com.armaninyow.quickcontainerinsert.QuickContainerInsert;

@Environment(EnvType.CLIENT)
public class QCIHudRenderer implements HudElement {

	private static final Identifier ID =
		Identifier.fromNamespaceAndPath(QuickContainerInsert.MOD_ID, "tooltip");

	public static void register() {
		HudElementRegistry.addLast(ID, new QCIHudRenderer());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor extractor, DeltaTracker deltaTracker) {
		if (!QCITooltipState.isActive()) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		ItemStack held = mc.player.getMainHandItem();
		if (held.isEmpty()) return;

		String containerName = QCITooltipState.getContainerName();
		Component msg;
		switch (QCITooltipState.getType()) {
			case CONTAINER_FULL ->
				msg = Component.translatable("tooltip.quickcontainerinsert.container_full", containerName);
			case ITEM_NOT_ALLOWED ->
				msg = Component.translatable("tooltip.quickcontainerinsert.item_not_allowed", containerName);
			default -> { return; }
		}

		int alpha = QCITooltipState.getAlpha();
		if (alpha <= 0) return;

		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		int x = screenWidth / 2;
		int y = screenHeight - 72;

		// Blend alpha into the red text color
		int color = (alpha << 24) | 0xFF5555;
		extractor.centeredText(mc.font, msg, x, y, color);
	}
}