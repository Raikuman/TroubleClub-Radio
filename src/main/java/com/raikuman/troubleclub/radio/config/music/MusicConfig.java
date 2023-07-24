package com.raikuman.troubleclub.radio.config.music;

import com.raikuman.botutilities.configs.ConfigInterface;
import com.raikuman.botutilities.configs.DatabaseConfigInterface;
import com.raikuman.botutilities.database.DatabaseManager;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *  Provides configuration for music
 *
 * @version 1.7 2023-29-06
 * @since 1.0
 */
public class MusicConfig implements ConfigInterface, DatabaseConfigInterface {

	private static final Logger logger = LoggerFactory.getLogger(MusicConfig.class);

	@Override
	public String fileName() {
		return "music_settings";
	}

	@Override
	public LinkedHashMap<String, String> getConfigs() {
		LinkedHashMap<String, String> configMap = new LinkedHashMap<>();
		configMap.put("track_1_vol", "50");
		configMap.put("track_2_vol", "50");
		configMap.put("track_3_vol", "50");

		return configMap;
	}

	@Override
	public void startup(JDA jda) {
		// Create music_setting table
		DatabaseManager.sendBasicQuery(
			// language=SQL
			"CREATE TABLE IF NOT EXISTS music_setting(music_setting_id INTEGER PRIMARY KEY AUTOINCREMENT, guild_id " +
				"INTEGER UNIQUE NOT NULL, track_1_vol INTEGER NOT NULL DEFAULT '50', track_2_vol INTEGER NOT NULL " +
				"DEFAULT " +
				"'50', track_3_vol INTEGER NOT NULL DEFAULT '50', FOREIGN KEY(guild_id) REFERENCES guild(guild_id))"
		);

		// Get all guild_id from guild table
		List<Integer> guildIds = new ArrayList<>();
		try (
			Connection connection = DatabaseManager.getConnection();
			Statement statement = connection.createStatement()
			) {
			try (ResultSet resultSet = statement.executeQuery(
				// language=SQL
				"SELECT guild_id FROM guild")
			) {
				while (resultSet.next()) {
					guildIds.add(resultSet.getInt(1));
				}
			}

			statement.closeOnCompletion();
		} catch (SQLException e) {
			logger.error("Could not retrieve guild_id column");
		}

		// Insert guild_id into music_setting table
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"INSERT OR IGNORE INTO music_setting(guild_id) VALUES(?)"
			)) {
			for (Integer guildId : guildIds) {
				preparedStatement.setString(1, String.valueOf(guildId));
				preparedStatement.addBatch();
			}

			preparedStatement.executeBatch();
		} catch (SQLException e) {
			logger.error("Could not add music setting for guilds " + guildIds);
		}
	}
}
