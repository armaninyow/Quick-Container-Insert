package com.armaninyow.quickcontainerinsert.util;

import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Tracks which players are currently in a QCI insert operation on the server,
 * so we can suppress the vanilla GUI-open / entity-interact packet that arrives
 * at the same time as our custom insert packet.
 */
public class QCIServerInsertFlag {

	private static final Set<ServerPlayer> insertingBlock =
		Collections.newSetFromMap(new WeakHashMap<>());
	private static final Set<ServerPlayer> insertingEntity =
		Collections.newSetFromMap(new WeakHashMap<>());

	public static void setInsertingBlock(ServerPlayer player) { insertingBlock.add(player); }
	public static void clearInsertingBlock(ServerPlayer player) { insertingBlock.remove(player); }
	public static boolean isInsertingBlock(ServerPlayer player) { return insertingBlock.contains(player); }

	public static void setInsertingEntity(ServerPlayer player) { insertingEntity.add(player); }
	public static void clearInsertingEntity(ServerPlayer player) { insertingEntity.remove(player); }
	public static boolean isInsertingEntity(ServerPlayer player) { return insertingEntity.contains(player); }
}