package com.raikuman.troubleclub.radio.config.music;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *  Provides configuration for music
 *
 * @version 1.6 2023-10-01
 * @since 1.0
 */
public class MusicConfig implements ConfigInterface, DatabaseConfigInterface {

	@Override
	public String fileName() {
		return "music_settings";
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
	public List<String> tableStatements() {
		// language=SQLITE-SQL
		return List.of(
			"CREATE TABLE IF NOT EXISTS music_settings(" +
			"guild_id VARCHAR(20) NOT NULL UNIQUE," +
			"volumetrack1 INTEGER NOT NULL DEFAULT '50'," +
			"volumetrack2 INTEGER NOT NULL DEFAULT '50'," +
			"volumetrack3 INTEGER NOT NULL DEFAULT '50'," +
			"FOREIGN KEY(guild_id) REFERENCES guild_settings(guild_id));"
		);
	}
}