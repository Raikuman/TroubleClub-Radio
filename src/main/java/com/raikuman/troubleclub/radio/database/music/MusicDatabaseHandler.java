package com.raikuman.troubleclub.radio.database.music;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.troubleclub.radio.config.music.MusicConfig;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MusicDatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(MusicDatabaseHandler.class);

    public static void addGuildSettings(Guild guild) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);

        // Couldn't retrieve guild id
        if (guildId == -1) {
            return;
        }

        // Add guild to music setting table
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO guild_music_setting(guild_id) VALUES(?)"
            )) {
            statement.setInt(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred adding guild to music setting table for: " + guild.getName());
        }
    }

    public static void removeGuildSettings(Guild guild) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);

        // Couldn't retrieve guild id
        if (guildId == -1) {
            return;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM guild_music_setting WHERE guild_id = ?"
            )) {
            statement.setInt(1, guildId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred deleting guild from music setting table for: " + guild.getName());
        }
    }

    public static int getVolume(Guild guild, int trackNum) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);

        // Couldn't retrieve guild id
        if (guildId == -1) {
            return getDefaultVolume();
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT track_" + trackNum + "_vol FROM guild_music_setting WHERE guild_id = ?"
            )) {
            statement.setInt(1, guildId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

            return getDefaultVolume();
        } catch (SQLException e) {
            logger.error("An error occurred getting volume for: " + guild.getName() + " with track: " + trackNum);
            return getDefaultVolume();
        }
    }

    public static void setVolume(Guild guild, int trackNum, int volume) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);

        // Couldn't retrieve guild id
        if (guildId == -1) {
            return;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE guild_music_setting SET track_" + trackNum + "_vol = ? WHERE guild_id = ?"
            )) {
            statement.setInt(1, volume);
            statement.setInt(2, guildId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred setting volume for: " + guild.getName() + " with track: " + trackNum);
        }
    }

    private static int getDefaultVolume() {
        try {
            return Integer.parseInt(new ConfigData(new MusicConfig()).getConfig("defaultvolume"));
        } catch (NumberFormatException e) {
            return 25;
        }
    }
}
