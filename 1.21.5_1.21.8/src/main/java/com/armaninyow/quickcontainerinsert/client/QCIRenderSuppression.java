package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class QCIRenderSuppression {
	public static final ThreadLocal<Boolean> SKIP_SUPPRESSION = ThreadLocal.withInitial(() -> false);
}