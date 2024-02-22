package com.raikuman.troubleclub.radio.database;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MusicDatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(MusicDatabaseHandler.class);

    public static void addGuildSettings(Guild guild) {
        int guildId = DefaultDatabaseHandler.getGuildId(guild);

        // Couldn't retrieve guild id
        if (guildId == -1) {
            return;
        };

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
        };

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
}
