package com.raikuman.troubleclub.radio.config.music;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DefaultDatabaseHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles getting and updating values of the music table in the database
 *
 * @version 1.4 2023-27-07
 * @since 1.2
 */
public class MusicDB {

	private static final Logger logger = LoggerFactory.getLogger(MusicDB.class);

	/**
	 * Returns the track volume from the database
	 * @param guild The Guild to get the config value from
	 * @param trackNum The track number to get the volume from
	 * @return The volume value
	 */
	public static int getTrackVolume(Guild guild, int trackNum) {
		int guildId = DefaultDatabaseHandler.getGuildId(guild);
		String track = "track_" + trackNum + "_vol";
		int[] volumes = new int[3];

		// Get volume from database
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT * FROM music_setting WHERE guild_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(guildId));

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					volumes[0] = resultSet.getInt(3);
					volumes[1] = resultSet.getInt(4);
					volumes[2] = resultSet.getInt(5);
				}
			}

			return volumes[trackNum - 1];
		} catch (SQLException e) {
			logger.error("Could not retrieve " + track + " for guild " + guild.getName() + ":" + guild.getId());
		}

		// Get volume from config
		try {
			return Integer.parseInt(ConfigIO.readConfig(new MusicConfig().fileName(), track));
		} catch (NumberFormatException e) {
			logger.error("Could not retrieve default " + track);
			return 25;
		}
	}

	/**
	 * Updates the track volume in the database
	 * @param guild The Guild to set the config value to
	 * @param trackNum The track number to set the volume to
	 * @param trackVolume The track volume to update the config to
	 */
	public static void setTrackVolume(Guild guild, int trackNum, int trackVolume) {
		int guildId = DefaultDatabaseHandler.getGuildId(guild);
		String track = "track_" + trackNum + "_vol";

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"UPDATE music_setting SET ? = ? WHERE guild_id = ?"
			)) {
			preparedStatement.setString(1, track);
			preparedStatement.setString(2, String.valueOf(trackVolume));
			preparedStatement.setString(3, String.valueOf(guildId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not update " + track + " for guild " + guild.getName() + ":" + guild.getId());
		}
	}
}
