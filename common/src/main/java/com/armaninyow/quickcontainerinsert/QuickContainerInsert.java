package com.armaninyow.quickcontainerinsert;

import com.armaninyow.quickcontainerinsert.config.QCIConfig;
import com.armaninyow.quickcontainerinsert.network.QCINetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickContainerInsert implements ModInitializer {
	public static final String MOD_ID = "quickcontainerinsert";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		QCIConfig.load();
		QCINetworking.registerServerPackets();
		LOGGER.info("Quick Container Insert initialized.");
	}
}