package com.armaninyow.quickcontainerinsert.config;

import com.armaninyow.quickcontainerinsert.QuickContainerInsert;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class QCIConfig {

	private static final ConfigClassHandler<QCIConfig> HANDLER = ConfigClassHandler.createBuilder(QCIConfig.class)
		.id(Identifier.fromNamespaceAndPath(QuickContainerInsert.MOD_ID, "config"))
		.serializer(config -> GsonConfigSerializerBuilder.create(config)
			.setPath(FabricLoader.getInstance().getConfigDir().resolve(QuickContainerInsert.MOD_ID + ".json"))
			.build())
		.build();

	@SerialEntry
	@AutoGen(category = "general")
	@Boolean
	public boolean enableAnimation = true;

	@SerialEntry
	@AutoGen(category = "general")
	@IntSlider(min = 1, max = 20, step = 1)
	public int transferSpeed = 4;

	@SerialEntry
	@AutoGen(category = "general")
	@IntSlider(min = 1, max = 20, step = 1)
	public int initialDelay = 10;

	public static void load() {
		HANDLER.load();
	}

	public static QCIConfig get() {
		return HANDLER.instance();
	}

	public static ConfigClassHandler<QCIConfig> handler() {
		return HANDLER;
	}

	public static boolean isAnimationEnabled() {
		return get().enableAnimation;
	}

	public static int getTransferSpeed() {
		return get().transferSpeed;
	}

	public static int getInitialDelay() {
		return get().initialDelay;
	}
}