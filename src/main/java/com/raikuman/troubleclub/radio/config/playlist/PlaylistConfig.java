package com.raikuman.troubleclub.radio.config.playlist;

import com.raikuman.botutilities.configs.DatabaseConfigInterface;
import com.raikuman.botutilities.database.DatabaseManager;
import net.dv8tion.jda.api.JDA;

/**
 *  Provides configuration for playlists
 *
 * @version 1.2 2023-24-07
 * @since 1.2
 */
public class PlaylistConfig implements DatabaseConfigInterface {

	@Override
	public void startup(JDA jda) {
		// Create user_playlist table
		DatabaseManager.sendBasicQuery(
			// language=SQL
			"CREATE TABLE IF NOT EXISTS user_playlist(user_playlist_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id " +
				"INTEGER NOT NULL, playlist_name VARCHAR(60), song_num INTEGER NOT NULL, FOREIGN KEY(user_id) " +
				"REFERENCES user(user_id))"
		);

		// Create playlist_song table
		DatabaseManager.sendBasicQuery(
			// language=SQL
			"CREATE TABLE IF NOT EXISTS playlist_song(playlist_song_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"user_playlist_id INTEGER NOT NULL, song_id INTEGER NOT NULL, song_num INTEGER NOT NULL, FOREIGN KEY" +
				"(song_id) REFERENCES song(song_id), CONSTRAINT fk_user_playlist_id FOREIGN KEY(user_playlist_id) REFERENCES " +
				"user_playlist(user_playlist_id) ON DELETE CASCADE)"
		);

		// Create song table
		DatabaseManager.sendBasicQuery(
			// language=SQL
			"CREATE TABLE IF NOT EXISTS song(song_id INTEGER PRIMARY KEY AUTOINCREMENT, song_link VARCHAR(20) UNIQUE NOT " +
				"NULL)"
		);
	}
}
