package com.armaninyow.quickcontainerinsert.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts Block.shouldRenderFace() to force rendering of faces adjacent
 * to our 8 QCI container blocks. This bypasses both the canOcclude() check
 * and the OCCLUSION_CACHE, which would otherwise hide e.g. the grass top
 * face when a barrel is placed on top.
 */
@Environment(EnvType.CLIENT)
@Mixin(Block.class)
public class BlockStateOcclusionMixin {

	@Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
	private static void qci$forceRenderFaceNextToQCIContainer(
		BlockState blockState, BlockGetter blockGetter, BlockPos blockPos,
		Direction direction, BlockPos blockPos2, CallbackInfoReturnable<Boolean> cir
	) {
		BlockState neighbor = blockGetter.getBlockState(blockPos2);
		Block neighborBlock = neighbor.getBlock();
		if (neighborBlock instanceof BarrelBlock
			|| neighborBlock instanceof HopperBlock
			|| neighborBlock instanceof DispenserBlock
			|| neighborBlock instanceof BrewingStandBlock
			|| neighborBlock instanceof AbstractFurnaceBlock) {
			cir.setReturnValue(true);
		}
	}
}