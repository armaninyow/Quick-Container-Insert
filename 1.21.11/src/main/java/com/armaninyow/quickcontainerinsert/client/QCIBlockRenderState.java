package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.level.Level;

@Environment(EnvType.CLIENT)
public class QCIBlockRenderState extends BlockEntityRenderState {
	public Level level;
}