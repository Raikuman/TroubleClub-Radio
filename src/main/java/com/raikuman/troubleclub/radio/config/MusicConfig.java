package com.raikuman.troubleclub.radio.config;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.LinkedHashMap;

/**
 *  Provides configuration for music
 *
 * @version 1.3 2022-29-06
 * @since 1.0
 */
public class MusicConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "musicSettings";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("volumetrack1", "50");
		configMap.put("volumetrack2", "50");
		configMap.put("volumetrack3", "50");

		return configMap;
	}
}
