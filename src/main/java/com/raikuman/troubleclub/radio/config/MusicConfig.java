package com.raikuman.troubleclub.radio.config;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.LinkedHashMap;

/**
 *  Provides configuration for music
 *
 * @version 1.4 2022-13-07
 * @since 1.0
 */
public class MusicConfig implements ConfigInterface, DatabaseConfigInterface {

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

	@Override
	public String tableName() {
		return "music_settings";
	}

	@Override
	public String tableStatement() {
		// language=SQLITE-SQL
		return "CREATE TABLE IF NOT EXISTS " + tableName() + "(" +
			"guild_id VARCHAR(20) NOT NULL," +
			"volumetrack1 INTEGER NOT NULL DEFAULT '50'," +
			"volumetrack2 INTEGER NOT NULL DEFAULT '50'," +
			"volumetrack3 INTEGER NOT NULL DEFAULT '50'," +
			"FOREIGN KEY(guild_id) REFERENCES guild_settings(guild_id));";
	}
}
