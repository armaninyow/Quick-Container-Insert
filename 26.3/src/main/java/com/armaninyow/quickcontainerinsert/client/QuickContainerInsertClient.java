package com.armaninyow.quickcontainerinsert.client;

import com.armaninyow.quickcontainerinsert.network.QCINetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

@Environment(EnvType.CLIENT)
public class QuickContainerInsertClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		QCINetworking.registerClientPackets();
		QCIClientHandler.register();
		QCIHudRenderer.register();

		BlockEntityRendererRegistry.register(BlockEntityTypes.BARREL, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.HOPPER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.DISPENSER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.DROPPER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.BREWING_STAND, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.FURNACE, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.BLAST_FURNACE, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityTypes.SMOKER, QCIBlockEntityRenderer::new);
	}
}