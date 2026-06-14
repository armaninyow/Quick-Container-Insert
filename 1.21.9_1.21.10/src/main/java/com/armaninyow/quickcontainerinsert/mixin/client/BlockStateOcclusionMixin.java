package com.armaninyow.quickcontainerinsert.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts Block.shouldRenderFace() to force rendering of faces adjacent
 * to our QCI container blocks. (1.21.2+ signature)
 */
@Environment(EnvType.CLIENT)
@Mixin(Block.class)
public class BlockStateOcclusionMixin {

	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private static void qci$forceRenderFaceNextToQCIContainer(
		BlockState blockState, BlockState neighborState,
		Direction direction, CallbackInfoReturnable<Boolean> cir
	) {
		Block neighborBlock = neighborState.getBlock();
		if (neighborBlock instanceof BarrelBlock
			|| neighborBlock instanceof HopperBlock
			|| neighborBlock instanceof DispenserBlock
			|| neighborBlock instanceof BrewingStandBlock
			|| neighborBlock instanceof AbstractFurnaceBlock) {
			cir.setReturnValue(true);
		}
	}
}