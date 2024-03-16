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
                    "songs INTEGER NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES user(user_id) ON DELETE CASCADE" +
                    ")"
            );

            statement.execute(
                "CREATE VIRTUAL TABLE IF NOT EXISTS playlist_fts USING fts5(" +
                    "playlist_id, user_id, name, songs" +
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

            // Triggers to update playlist_fts
            statement.execute(
                "CREATE TRIGGER IF NOT EXISTS insert_playlist_fts AFTER INSERT ON playlist " +
                    "BEGIN " +
                    "INSERT INTO playlist_fts(playlist_id, user_id, name, songs) VALUES (NEW.playlist_id, NEW.user_id, NEW.name, NEW.songs); " +
                    "END"
            );

            statement.execute(
                "CREATE TRIGGER IF NOT EXISTS update_playlist_fts AFTER UPDATE ON playlist " +
                    "BEGIN " +
                    "UPDATE playlist_fts SET " +
                    "user_id = NEW.user_id, name = NEW.name, songs = NEW.songs " +
                    "WHERE playlist_id = NEW.playlist_id; " +
                    "END"
            );

            statement.execute(
                "CREATE TRIGGER IF NOT EXISTS delete_playlist_fts AFTER DELETE ON playlist " +
                    "BEGIN " +
                    "DELETE FROM playlist_fts WHERE playlist_id = OLD.playlist_id; " +
                    "END"
            );

        } catch (SQLException e) {
            logger.error("An error occurred creating playlist tables");
        }
    }
}
