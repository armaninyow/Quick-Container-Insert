package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Simple flag set on the client while a QCI insert packet is in-flight,
 * so other systems (e.g. swap animation suppression) can check it.
 */
@Environment(EnvType.CLIENT)
public class QCIInsertState {

	private static boolean inserting = false;

	public static void setInserting(boolean value) {
		inserting = value;
	}

	public static boolean isInserting() {
		return inserting;
	}
}