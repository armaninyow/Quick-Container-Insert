package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.WeakHashMap;

/**
 * Maps entity render state instances to their entity IDs so that
 * renderer mixins can look up active animations by entity ID.
 */
@Environment(EnvType.CLIENT)
public class QCIEntityRenderStateTracker {

	private static final WeakHashMap<EntityRenderState, Integer> stateToId = new WeakHashMap<>();

	public static void track(EntityRenderState state, int entityId) {
		stateToId.put(state, entityId);
	}

	public static Integer getEntityId(EntityRenderState state) {
		return stateToId.get(state);
	}
}