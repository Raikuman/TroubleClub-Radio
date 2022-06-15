package com.raiku.troubleclub.radio.config.defaults;

import com.raiku.troubleclub.radio.config.ConfigInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Default config for bot
 *
 * @version 1.0 2022-15-06
 * @since 1.0
 */
public class BotConfig implements ConfigInterface {

	@Override
	public String fileName() {
		return "settings.cfg";
	}

	@Override
	public HashMap<String, String> getConfigs() {
		return new HashMap<>(Map.ofEntries(
			Map.entry("prefix", "!!")
		));
	}
}
