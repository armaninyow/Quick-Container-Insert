package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * A minimal BlockAndTintGetter that presents a single block state at one
 * position and AIR everywhere else. This prevents renderBatched from culling
 * any faces due to solid neighbors, so all 6 faces always render.
 */
@Environment(EnvType.CLIENT)
public class QCISingleBlockView implements BlockAndTintGetter {

	private final BlockAndTintGetter realLevel;
	private final BlockPos targetPos;
	private final BlockState targetState;

	public QCISingleBlockView(BlockAndTintGetter realLevel, BlockPos pos, BlockState state) {
		this.realLevel = realLevel;
		this.targetPos = pos;
		this.targetState = state;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		if (pos.equals(targetPos)) return targetState;
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return Fluids.EMPTY.defaultFluidState();
	}

	@Override
	@Nullable
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getBrightness(LightLayer lightLayer, BlockPos pos) {
		return realLevel.getBrightness(lightLayer, pos);
	}

	@Override
	public LevelLightEngine getLightEngine() {
		return realLevel.getLightEngine();
	}

	@Override
	public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
		return realLevel.getBlockTint(pos, colorResolver);
	}

	@Override
	public int getHeight() {
		return realLevel.getHeight();
	}

	@Override
	public int getMinBuildHeight() {
		return realLevel.getMinBuildHeight();
	}

	@Override
	public float getShade(Direction direction, boolean shade) {
		return realLevel.getShade(direction, shade);
	}

}