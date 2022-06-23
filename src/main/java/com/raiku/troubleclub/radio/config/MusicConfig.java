package com.raiku.troubleclub.radio.config;

import com.raikuman.botutilities.configs.ConfigInterface;

import java.util.HashMap;
import java.util.Map;

public class MusicConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "musicSettings";
	}

	@Override
	public HashMap<String, String> getConfigs() {
		return new HashMap<>(Map.ofEntries(
			Map.entry("volume", "25")
		));
	}
}
