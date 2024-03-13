package com.raikuman.troubleclub.radio.config;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DatabaseStartup;
import net.dv8tion.jda.api.JDA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class PlaylistStartup implements DatabaseStartup {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistStartup.class);

    @Override
    public void startup(JDA jda) {
        try (
            Connection connection = DatabaseManager.getConnection();
            Statement statement = connection.createStatement()
            ) {
            // Create song table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS song(" +
                    "song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "encoded TEXT NOT NULL UNIQUE" +
                    ")"
            );

            // Create playlist table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS playlist(" +
                    "playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "name VARCHAR(20)," +
                    "link VARCHAR(20)," +
                    "songs INTEGER NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE" +
                    ")"
            );

            // Create playlist song table
            statement.execute(
                "CREATE TABLE IF NOT EXISTS playlist_song(" +
                    "playlist_song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "playlist_id INTEGER NOT NULL," +
                    "song_id INTEGER NOT NULL," +
                    "FOREIGN KEY(playlist_id) REFERENCES playlist(playlist_id) ON DELETE CASCADE," +
                    "FOREIGN KEY(song_id) REFERENCES song(song_id)" +
                    ")"
            );

            statement.execute(
                "CREATE TRIGGER IF NOT EXISTS trim_songs AFTER DELETE ON playlist_song " +
                    "BEGIN " +
                    "DELETE FROM song WHERE song_id = OLD.song_id AND NOT EXISTS (SELECT 1 FROM playlist_song WHERE " +
                    "song_id = OLD.song_id); " +
                    "END"
            );
        } catch (SQLException e) {
            logger.error("An error occurred creating playlist tables");
        }
    }
}
