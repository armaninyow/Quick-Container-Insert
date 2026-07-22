package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.WeakHashMap;

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