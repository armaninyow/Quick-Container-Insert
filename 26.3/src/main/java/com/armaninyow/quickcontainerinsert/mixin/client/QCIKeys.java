package com.armaninyow.quickcontainerinsert.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class QCIKeys {

	private QCIKeys() {}

	public static boolean isCtrlDown() {
		return InputConstants.isKeyDown(InputConstants.KEY_LCONTROL);
	}
}