package com.armaninyow.quickcontainerinsert.util;

import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class ContainerUtil {

	public static boolean isContainerBlock(Block block) {
		if (block instanceof AbstractFurnaceBlock) return true;
		if (block instanceof ChestBlock) return true;
		if (block instanceof TrappedChestBlock) return true;
		if (block instanceof BarrelBlock) return true;
		if (block instanceof ShulkerBoxBlock) return true;
		if (block instanceof HopperBlock) return true;
		if (block instanceof DispenserBlock) return true; // covers Dropper too
		if (block instanceof BrewingStandBlock) return true;
		if (block instanceof EnderChestBlock) return true;
		String name = net.minecraft.core.registries.BuiltInRegistries.BLOCK
			.getKey(block).getPath();
		if (name.contains("copper") && name.contains("chest")) return true;
		return false;
	}

	public static boolean isContainerEntity(Entity entity) {
		return entity instanceof ContainerEntity;
	}

	public static Container getContainer(BlockEntity be, Direction face) {
		if (be == null) return null;
		if (be instanceof EnderChestBlockEntity) return null;

		if (be instanceof ChestBlockEntity && be.getLevel() != null) {
			BlockState state = be.getBlockState();
			if (state.getBlock() instanceof ChestBlock chestBlock
				&& state.hasProperty(ChestBlock.TYPE)
				&& state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
				Container merged = chestBlock.getContainer(
					chestBlock, state, be.getLevel(), be.getBlockPos(), true);
				if (merged != null) return merged;
			}
		}

		if (be instanceof Container c) return c;
		return null;
	}

	public static boolean isItemAllowed(Container container, ItemStack stack) {
		int size = container.getContainerSize();
		for (int i = 0; i < size; i++) {
			if (container.canPlaceItem(i, stack)) return true;
		}
		return false;
	}

	public static int getBrewingStandTargetSlot(BrewingStandBlockEntity be, ItemStack stack) {
		if (stack.is(Items.BLAZE_POWDER)) {
			ItemStack fuel = be.getItem(4);
			if (fuel.isEmpty() || (ItemStack.isSameItemSameComponents(fuel, stack) && fuel.getCount() < fuel.getMaxStackSize())) {
				return 4;
			}
			return -1;
		}
		if (isBrewingBottle(stack)) {
			for (int i = 0; i < 3; i++) {
				ItemStack slot = be.getItem(i);
				if (slot.isEmpty()) return i;
			}
			return -1;
		}
		if (!be.canPlaceItem(3, stack)) return -1;
		ItemStack ingredient = be.getItem(3);
		if (ingredient.isEmpty() || (ItemStack.isSameItemSameComponents(ingredient, stack) && ingredient.getCount() < ingredient.getMaxStackSize())) {
			return 3;
		}
		return -1;
	}

	private static boolean isBrewingBottle(ItemStack stack) {
		return stack.is(Items.GLASS_BOTTLE)
			|| stack.is(Items.WATER_BUCKET)
			|| stack.getItem() instanceof net.minecraft.world.item.PotionItem
			|| stack.getItem() instanceof net.minecraft.world.item.SplashPotionItem
			|| stack.getItem() instanceof net.minecraft.world.item.LingeringPotionItem;
	}

	public static int getFurnaceTargetSlot(AbstractFurnaceBlockEntity be, ItemStack stack) {
		if (be.canPlaceItem(1, stack)) {
			ItemStack fuelSlot = be.getItem(1);
			if (fuelSlot.isEmpty() || (ItemStack.isSameItemSameComponents(fuelSlot, stack) && fuelSlot.getCount() < fuelSlot.getMaxStackSize())) {
				return 1;
			}
		}
		ItemStack inputSlot = be.getItem(0);
		if (inputSlot.isEmpty() || (ItemStack.isSameItemSameComponents(inputSlot, stack) && inputSlot.getCount() < inputSlot.getMaxStackSize())) {
			return 0;
		}
		return -1;
	}

	public static ItemStack insertItem(Container container, ItemStack stack) {
		int size = container.getContainerSize();
		for (int i = 0; i < size; i++) {
			ItemStack slot = container.getItem(i);
			if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
				int maxStack = Math.min(slot.getMaxStackSize(), container.getMaxStackSize());
				if (slot.getCount() < maxStack && container.canPlaceItem(i, stack)) {
					slot.grow(1);
					container.setChanged();
					return ItemStack.EMPTY;
				}
			}
		}
		for (int i = 0; i < size; i++) {
			ItemStack slot = container.getItem(i);
			if (slot.isEmpty() && container.canPlaceItem(i, stack)) {
				container.setItem(i, stack.copy());
				container.setChanged();
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}

	public static ItemStack insertItemIntoSlot(Container container, ItemStack stack, int slot) {
		ItemStack existing = container.getItem(slot);
		if (existing.isEmpty()) {
			container.setItem(slot, stack.copy());
			container.setChanged();
			return ItemStack.EMPTY;
		} else if (ItemStack.isSameItemSameComponents(existing, stack)) {
			int maxStack = Math.min(existing.getMaxStackSize(), container.getMaxStackSize());
			if (existing.getCount() < maxStack) {
				existing.grow(1);
				container.setChanged();
				return ItemStack.EMPTY;
			}
		}
		return stack;
	}
}