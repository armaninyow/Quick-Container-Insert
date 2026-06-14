package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class QCIContainerRendererRegistry {

	private static final Set<Block> BLOCKS = new HashSet<>();

	public static void register(Block block) {
		BLOCKS.add(block);
	}

	public static boolean hasRenderer(Block block) {
		return BLOCKS.contains(block);
	}
}