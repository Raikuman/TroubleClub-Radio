package com.raikuman.troubleclub.radio.config.playlist;

import com.raikuman.botutilities.configs.DatabaseConfigInterface;

import java.util.List;

/**
 *  Provides configuration for playlists
 *
 * @version 1.0 2023-10-01
 * @since 1.2
 */
public class PlaylistConfig implements DatabaseConfigInterface {

	@Override
	public List<String> tableStatements() {
		// language=SQLITE-SQL
		return List.of(
			"CREATE TABLE IF NOT EXISTS playlists(" +
			"playlist_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"member_id INTEGER NOT NULL," +
			"playlist_name VARCHAR(20)," +
			"song_count INTEGER NOT NULL," +
			"FOREIGN KEY(member_id) REFERENCES members(member_id));",

			"CREATE TABLE IF NOT EXISTS songs(" +
			"song_id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"song_link VARCHAR(20) NOT NULL UNIQUE);",

			"CREATE TABLE IF NOT EXISTS playlists_songs(" +
			"playlist_number INTEGER NOT NULL," +
			"song_id INTEGER NOT NULL," +
			"FOREIGN KEY(playlist_number) REFERENCES playlists(playlist_id)," +
			"FOREIGN KEY(song_id) REFERENCES songs(song_id));"
		);
	}
}
