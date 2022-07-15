package com.raikuman.troubleclub.radio.config.music;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.database.DatabaseIO;
import com.raikuman.botutilities.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles getting and updating values of the music table in the database
 *
 * @version 1.0 2022-13-07
 * @since 1.2
 */
public class MusicDB {

	private static final Logger logger = LoggerFactory.getLogger(MusicDB.class);

	/**
	 * Returns the track volume from the database
	 * @param guildId The guild id to get the config value from
	 * @param trackNum The track number to get the volume from
	 * @return The volume value
	 */
	public static String getTrackVolume(long guildId, int trackNum) {
		String track = "volumetrack" + trackNum;

		// language=SQLITE-SQL
		String config = DatabaseIO.getConfig(
			"SELECT music_settings." + track + " " +
				"FROM settings, music_settings " +
				"WHERE settings.guild_id = music_settings.guild_id " +
				"AND settings.guild_id = ?",
			guildId,
			track
		);

		if (config != null)
			return config;

		setDefault(guildId);

		return ConfigIO.readConfig("music_settings", track);
	}

	/**
	 * Updates the track volume in the database
	 * @param guildId The guild id to set the config value to
	 * @param trackNum The track number to set the volume to
	 * @param trackVolume The track volume to update the config to
	 */
	public static void updateTrackVolume(long guildId, int trackNum, int trackVolume) {
		String track = "volumetrack" + trackNum;

		// language=SQLITE-SQL
		boolean updated = DatabaseIO.updateConfig(
			"UPDATE music_settings " +
				"SET " + track + " = ? " +
				"WHERE music_settings.guild_id = ?",
			guildId,
			String.valueOf(trackVolume)
		);

		if (!updated)
			setDefault(guildId);
	}

	/**
	 * Sets the default entry for music in the database
	 * @param guildId The guild id to set the default entry to
	 */
	private static void setDefault(long guildId) {
		try (final PreparedStatement preparedStatement = DatabaseManager
			.getConnection()
			// language=SQLITE-SQL
			.prepareStatement(
				"INSERT INTO music_settings(guild_id) VALUES(?)"
			)) {

			preparedStatement.setString(1, String.valueOf(guildId));
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.warn("Could not insert " + new MusicConfig() + " to database");
		}
	}
}
