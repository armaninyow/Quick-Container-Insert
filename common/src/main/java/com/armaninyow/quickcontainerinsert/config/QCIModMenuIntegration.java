package com.armaninyow.quickcontainerinsert.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;

@Environment(EnvType.CLIENT)
public class QCIModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> buildScreen(parent);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static Screen buildScreen(Screen parent) {
		return (Screen) AutoConfig.getConfigScreen((Class) QCIConfig.class, parent).get();
	}
}