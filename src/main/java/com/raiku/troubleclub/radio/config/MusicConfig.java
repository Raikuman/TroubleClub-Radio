package com.raiku.troubleclub.radio.config;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.HashMap;
import java.util.Map;

/**
 *  Provides configuration for music
 *
 * @version 1.0 2022-23-06
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
			Map.entry("volume", "100")
		));
	}
}
