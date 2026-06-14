package com.armaninyow.quickcontainerinsert.mixin.client;

import com.armaninyow.quickcontainerinsert.util.ContainerUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Returns INVISIBLE for QCI container blocks so they are never baked into
 * the chunk mesh. QCIBlockEntityRenderer handles all rendering for them in 1.21.4+.
 */
@Environment(EnvType.CLIENT)
@Mixin(BlockBehaviour.class)
public class BlockStateRenderShapeMixin {

	@Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
	private void qci$useInvisibleRender(BlockState state, CallbackInfoReturnable<RenderShape> cir) {
		if (ContainerUtil.isContainerBlock(state.getBlock())) {
			cir.setReturnValue(RenderShape.INVISIBLE);
		}
	}
}