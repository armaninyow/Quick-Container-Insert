package com.armaninyow.quickcontainerinsert.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Targets the exact block classes that override getRenderShape() returning MODEL.
 * Returns ENTITYBLOCK_ANIMATED so SectionCompiler skips chunk baking.
 * QCIBlockEntityRenderer handles drawing them instead.
 */
@Environment(EnvType.CLIENT)
@Mixin({
	BarrelBlock.class,
	HopperBlock.class,
	DispenserBlock.class,
	BrewingStandBlock.class,
	AbstractFurnaceBlock.class
})
public class BlockStateRenderShapeMixin {

	@Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
	private void qci$useEntityRender(BlockState state, CallbackInfoReturnable<RenderShape> cir) {
		cir.setReturnValue(RenderShape.ENTITYBLOCK_ANIMATED);
	}
}