package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCITooltipState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public class GuiHudMixin {

	@Inject(
		method = "render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V",
		at = @At("TAIL")
	)
	private void qci$renderContainerMessage(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
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

		// Blend alpha into the red text color: 0xRRGGBB -> 0xAARRGGBB
		int color = (alpha << 24) | 0xFF5555;
		graphics.drawCenteredString(mc.font, msg, x, y, color);
	}
}