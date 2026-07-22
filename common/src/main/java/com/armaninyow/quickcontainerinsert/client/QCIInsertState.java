package com.armaninyow.quickcontainerinsert.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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