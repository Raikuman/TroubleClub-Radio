package com.raikuman.troubleclub.radio.config.music;

import com.raikuman.botutilities.config.ConfigData;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DatabaseStartup;
import com.raikuman.troubleclub.radio.database.music.MusicDatabaseHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MusicStartup implements DatabaseStartup {

    private static final Logger logger = LoggerFactory.getLogger(MusicStartup.class);

    @Override
    public void startup(JDA jda) {
        // Setup tables
        if (!setupTables()) return;

        populateTables(jda);
    }

    private boolean setupTables() {
        // Default volume
        int volume;
        try {
            volume = Integer.parseInt(new ConfigData(new MusicConfig()).getConfig("defaultvolume"));
        } catch (NumberFormatException e) {
            volume = 25;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(
                "CREATE TABLE IF NOT EXISTS guild_music_setting(" +
                    "setting_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id INTEGER UNIQUE NOT NULL," +
                    "track_1_vol INTEGER NOT NULL DEFAULT '" + volume + "'," +
                    "track_2_vol INTEGER NOT NULL DEFAULT '" + volume + "'," +
                    "track_3_vol INTEGER NOT NULL DEFAULT '" + volume + "'," +
                    "FOREIGN KEY(guild_id) REFERENCES guild(guild_id) ON DELETE CASCADE" +
                    ")"
            );
            return true;
        } catch (SQLException e) {
            logger.error("An error occurred creating guild music setting tables");
            return false;
        }
    }

    private void populateTables(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            MusicDatabaseHandler.addGuildSettings(guild);
        }
    }
}
