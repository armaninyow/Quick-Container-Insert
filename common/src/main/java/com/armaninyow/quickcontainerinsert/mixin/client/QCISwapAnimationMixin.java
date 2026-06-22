package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.client.QCIInsertState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(ItemInHandRenderer.class)
public class QCISwapAnimationMixin {

	@Shadow
	private ItemStack mainHandItem;

	/**
	 * Redirects the player.getMainHandItem() call inside tick() so that
	 * during a QCI insert we return the already-cached mainHandItem reference
	 * instead of the new instance the server sent. This keeps the reference
	 * comparison (this.mainHandItem != nextMainHand) false, preventing the
	 * swap/dip animation from triggering.
	 */
	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;",
			ordinal = 0
		)
	)
	private ItemStack qci$suppressSwapAnimation(LocalPlayer player) {
		if (QCIInsertState.isInserting()) return this.mainHandItem;
		return player.getMainHandItem();
	}
}