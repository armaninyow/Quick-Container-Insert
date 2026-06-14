package com.armaninyow.quickcontainerinsert.client;

import com.armaninyow.quickcontainerinsert.network.QCINetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;

@Environment(EnvType.CLIENT)
public class QuickContainerInsertClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		QCINetworking.registerClientPackets();
		QCIClientHandler.register();

		BlockEntityRendererRegistry.register(BlockEntityType.BARREL, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.HOPPER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.DISPENSER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.DROPPER, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.BREWING_STAND, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.FURNACE, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.BLAST_FURNACE, QCIBlockEntityRenderer::new);
		BlockEntityRendererRegistry.register(BlockEntityType.SMOKER, QCIBlockEntityRenderer::new);
	}
}