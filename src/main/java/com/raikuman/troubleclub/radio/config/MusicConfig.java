package com.raikuman.troubleclub.radio.config;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.HashMap;
import java.util.Map;

/**
 *  Provides configuration for music
 *
 * @version 1.2 2022-29-06
 * @since 1.0
 */
public class MusicConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "musicSettings";
	}

	@Override
	public HashMap<String, String> getConfigs() {
		return new HashMap<>(Map.ofEntries(
			Map.entry("volumetrack1", "50"),
			Map.entry("volumetrack2", "50"),
			Map.entry("volumetrack3", "50")
		));
	}
}
