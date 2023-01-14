package com.raikuman.troubleclub.radio.config.playlist;

import com.raikuman.botutilities.database.DatabaseIO;
import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles getting and updating values of the playlist tables in the database
 *
 * @version 1.3 2023-13-01
 * @since 1.2
 */
public class PlaylistDB {

	private static final Logger logger = LoggerFactory.getLogger(PlaylistDB.class);

	/**
	 * Creates a playlist using the music queue, propagating the playlist tables with relevant playlist
	 * information
	 * @param playlistInfo The PlaylistInfo object with playlist information
	 */
	public static int createPlaylistQueue(PlaylistInfo playlistInfo) {
		// Add songs to song table
		addSongs(playlistInfo.getSongs());

		// Get song ids from songs table
		List<String> songIds = getSongIds(playlistInfo.getSongs());
		if (songIds.isEmpty()) {
			removeSongs(playlistInfo.getSongs());
			return 1;
		}

		List<Integer> integerIds = songIds.stream()
			.map(Integer::parseInt).sorted().collect(Collectors.toList());

		songIds = integerIds.stream().map(s -> Integer.toString(s)).collect(Collectors.toList());

		// Add playlist to playlist table and get its id
		int playlistId = addPlaylist(playlistInfo);
		if (playlistId < 1) {
			// Prompt error
			removeSongs(playlistInfo.getSongs());
			return 2;
		}

		// Create playlist song entry in playlist song table
		if (!addPlaylistsSongs(playlistId, songIds)) {
			removeSongs(playlistInfo.getSongs());
			removePlaylist(playlistId);
		}

		return 0;
	}

	/**
	 * Creates a playlist using a YouTune playlist link, using only the playlists table
	 * @param playlistInfo The PlaylistInfo object with playlist information
	 */
	public static int createPlaylistLink(PlaylistInfo playlistInfo) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(playlistInfo.getMemberId())
		);

		if (memberId.isEmpty())
			return 1;

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO playlists(member_id, playlist_name, song_count, playlist_link) " +
					"VALUES(?, ?, ?, ?)")
			) {

			// Insert to playlists table
			preparedStatement.setString(1, memberId);
			preparedStatement.setString(2, playlistInfo.getName());
			preparedStatement.setString(3, String.valueOf(playlistInfo.getNumSongs()));
			preparedStatement.setString(4, playlistInfo.getPlaylistLink());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add playlist link to the playlists table");
			return 2;
		}

		return 0;
	}

	/**
	 * Adds song urls to the songs table
	 * @param songUrls The list of song urls to add to the database
	 */
	private static void addSongs(List<String> songUrls) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO songs(song_link) VALUES(?)")
			) {

			for (String url : songUrls) {
				preparedStatement.setString(1, url);
				preparedStatement.addBatch();
				preparedStatement.clearParameters();
			}

			preparedStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add song to the songs table");
		}
	}

	/**
	 * Retrieves song ids from the songs table using song urls
	 * @param songUrls The list of song urls to query the database
	 * @return The list of song ids
	 */
	private static List<String> getSongIds(List<String> songUrls) {
		// Generate IN clause to get recent Ids
		StringBuilder inClause = new StringBuilder("IN(");
		int count = 1;
		for (String url : songUrls) {
			inClause
				.append("'")
				.append(url)
				.append("'");
			if (count < songUrls.size())
				inClause.append(", ");

			count++;
		}
		inClause.append(");");

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT song_id FROM songs WHERE song_link " + inClause)
		) {
			List<String> songIds = new ArrayList<>();
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					songIds.add(resultSet.getString("song_id"));
				}
			}

			return songIds;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not retrieve song links from ids");
		}

		return new ArrayList<>();
	}

	/**
	 * Add playlist information to the playlists table
	 * @param playlistInfo The PlaylistInfo object with playlist information
	 * @return The playlist id
	 */
	private static int addPlaylist(PlaylistInfo playlistInfo) {
		// Get member id from members table
		// language=SQLITE-SQL
		String memberId = DatabaseIO.getConfig(
			"SELECT members.member_id " +
				"FROM members " +
				"WHERE members.member_long = ?",
			"member_id",
			String.valueOf(playlistInfo.getMemberId())
		);

		if (memberId.isEmpty())
			return 0;

		// Create playlist entry in playlist table
		String playlistId = "";
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO playlists(member_id, playlist_name, song_count) VALUES(?, ?, ?)");

			PreparedStatement lastInsertStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT last_insert_rowid();")
		) {

			// Insert to playlists table
			preparedStatement.setString(1, memberId);
			preparedStatement.setString(2, playlistInfo.getName());
			preparedStatement.setString(3, String.valueOf(playlistInfo.getSongs().size()));
			preparedStatement.execute();

			// Get playlist id of recently inserted playlist
			try (ResultSet resultSet = lastInsertStatement.executeQuery()) {
				playlistId = resultSet.getString("last_insert_rowid()");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add entry to the playlists table");
		}

		try {
			return Integer.parseInt(playlistId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error("Could not parse result of playlist id");
		}

		return 0;
	}

	/**
	 * Adds song ids relevant to the playlist id to the playlists_songs table
	 * @param playlistId The id of the playlist
	 * @param songIds The list of song ids related to the playlist
	 */
	private static boolean addPlaylistsSongs(int playlistId, List<String> songIds) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO playlists_songs(playlist_number, song_id) VALUES(?, ?)")
		) {
			for (String ids : songIds) {
				preparedStatement.setString(1, String.valueOf(playlistId));
				preparedStatement.setString(2, ids);
				preparedStatement.addBatch();
				preparedStatement.clearParameters();
			}

			preparedStatement.executeBatch();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add song/playlist pair to the playlists_songs table");
		}

		return false;
	}

	/**
	 * Returns the playlist information of a given playlist from a user
	 * @param memberId The user id to query the database
	 * @param playlistNum The playlist number given by the user
	 * @return The PlaylistInfo object with playlist information
	 */
	public static PlaylistInfo getPlaylist(long memberId, int playlistNum) {
		try (
			Connection connection = DatabaseManager.getConnection();

			PreparedStatement playlistStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT playlists.playlist_id, playlists.playlist_name, playlists.playlist_link " +
				"FROM playlists " +
				"INNER JOIN members ON playlists.member_id=members.member_id " +
				"WHERE members.member_long=?");

			PreparedStatement songStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT song_link " +
					"FROM songs " +
					"INNER JOIN playlists_songs ON songs.song_id=playlists_songs.song_id " +
					"WHERE playlists_songs.playlist_number=?")
			) {

			playlistStatement.setString(1, String.valueOf(memberId));

			HashMap<Integer, PlaylistInfo> playlistMap = new LinkedHashMap<>();
			try (ResultSet resultSet = playlistStatement.executeQuery()) {
				while (resultSet.next()) {
					int playlistId;
					try {
						playlistId = Integer.parseInt(resultSet.getString("playlist_id"));
					} catch (NumberFormatException e) {
						logger.error("Could not parse playlist id");
						continue;
					}

					String link;
					if (resultSet.getString("playlist_link") == null)
						link = "";
					else
						link = resultSet.getString("playlist_link");

					playlistMap.put(
						playlistId,
						new PlaylistInfo(
							resultSet.getString("playlist_name"),
							0,
							new ArrayList<>(),
							link,
							memberId
						)
					);
				}
			}

			if (playlistNum > playlistMap.size()) {
				logger.error("Playlist num does not exist in user's playlists");
				return null;
			}

			int playlistCounter = 0;
			int playlistId = 0;
			for (Map.Entry<Integer, PlaylistInfo> entry : playlistMap.entrySet()) {
				if (playlistCounter == (playlistNum - 1)) {
					playlistId = entry.getKey();
					break;
				}

				playlistCounter++;
			}

			if (playlistId == 0) {
				logger.error("Could not retrieve playlist id from map");
				return null;
			}

			// Playlist from songs
			if (playlistMap.get(playlistId).getPlaylistLink().isEmpty()) {
				songStatement.setString(1, String.valueOf(playlistId));

				List<String> songLinks = new ArrayList<>();
				try (ResultSet resultSet = songStatement.executeQuery()) {
					while (resultSet.next()) {
						songLinks.add(resultSet.getString("song_link"));
					}
				}

				if (songLinks.isEmpty()) {
					logger.error("Could not retrieve song links from playlist id");
					return null;
				}

				String playlistName = playlistMap.get(playlistId).getName();
				if (playlistMap.get(playlistId).getName().equalsIgnoreCase("Unnamed Cassette")) {
					playlistName = "Cassette #" + playlistNum;
				}

				return new PlaylistInfo(playlistName, songLinks.size(), songLinks, memberId);
			}

			// Playlist from link
			return new PlaylistInfo(
				playlistMap.get(playlistId).getName(),
				playlistMap.get(playlistId).getPlaylistLink(),
				memberId
			);

		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not retrieve playlist from database");
		}

		return null;
	}

	/**
	 * Removes songs from the database using a list of urls
	 * @param songUrls The list of song urls to delete from the database
	 */
	private static void removeSongs(List<String> songUrls) {
		// Generate IN clause to get recent Ids
		StringBuilder inClause = new StringBuilder("IN(");
		int count = 1;
		for (String url : songUrls) {
			inClause
				.append("'")
				.append(url)
				.append("'");
			if (count < songUrls.size())
				inClause.append(", ");

			count++;
		}
		inClause.append(");");

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"DELETE FROM songs " +
				"WHERE song_link " + inClause)
			) {
			preparedStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not remove songs to the songs table");
		}
	}

	/**
	 * Removes a playlist from the database using the playlist id
	 * @param playlistId The playlist id to remove from the database
	 * @return True if removal was success, false otherwise
	 */
	private static boolean removePlaylist(int playlistId) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"DELETE FROM playlists " +
					"WHERE playlist_id=?")
		) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not delete playlist from the playlists table");
			return false;
		}
	}

	/**
	 * Removes song ids relevant to the playlist id from the playlists_songs table
	 * @param playlistId The playlist id to remove from the database
	 * @return True if removal was success, false otherwise
	 */
	private static boolean removePlaylistsSongs(int playlistId) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"DELETE FROM playlists_songs " +
					"WHERE playlist_number=?")
			) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.execute();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not remove songs from playlists songs table");
			return false;
		}
	}

	/**
	 * Deletes a playlist and its relevant information from the database tables
	 * @param playlistNum The playlist number given by the user
	 * @param memberId The member id to delete the playlist from
	 * @return True if the playlist and information was deleted, false otherwise
	 */
	public static boolean deletePlaylist(int playlistNum, long memberId) {
		int playlistId = 0;
		PlaylistInfo foundPlaylist = null;

		try (
			Connection connection = DatabaseManager.getConnection();

			PreparedStatement playlistStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT playlists.playlist_id, playlists.playlist_name, playlists.playlist_link " +
					"FROM playlists " +
					"INNER JOIN members ON playlists.member_id=members.member_id " +
					"WHERE members.member_long=?")
			) {

			playlistStatement.setString(1, String.valueOf(memberId));

			HashMap<Integer, PlaylistInfo> playlistMap = new LinkedHashMap<>();
			try (ResultSet resultSet = playlistStatement.executeQuery()) {
				while (resultSet.next()) {
					int currPlaylistId;
					try {
						currPlaylistId = Integer.parseInt(resultSet.getString("playlist_id"));
					} catch (NumberFormatException e) {
						logger.error("Could not parse playlist id");
						continue;
					}

					String link;
					if (resultSet.getString("playlist_link") == null)
						link = "";
					else
						link = resultSet.getString("playlist_link");

					playlistMap.put(
						currPlaylistId,
						new PlaylistInfo(
							resultSet.getString("playlist_name"),
							0,
							new ArrayList<>(),
							link,
							memberId
						)
					);
				}
			}

			if (playlistNum > playlistMap.size()) {
				logger.error("Playlist num does not exist in user's playlists");
				return false;
			}

			int playlistCounter = 0;
			for (Map.Entry<Integer, PlaylistInfo> entry : playlistMap.entrySet()) {
				if (playlistCounter == (playlistNum - 1)) {
					playlistId = entry.getKey();
					foundPlaylist = entry.getValue();
					break;
				}

				playlistCounter++;
			}

			if (playlistId == 0) {
				logger.error("Could not retrieve playlist id from map");
				return false;
			}

			if (foundPlaylist == null) {
				logger.error("Could not retrieve playlist info from map");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not retrieve playlist id from the database");
			return false;
		}

		// Remove playlist normally
		if (foundPlaylist.getPlaylistLink().isEmpty()) {
			if (!removePlaylist(playlistId))
				return false;

			return removePlaylistsSongs(playlistId);
		// Remove playlist with link
		} else {
			return removePlaylist(playlistId);
		}
	}

	/**
	 * Renames a user's playlist
	 * @param playlistNum The playlist number to change the name of
	 * @param memberId The member id of the playlist to change
	 * @param playlistName The name to change the playlist to
	 * @return True if the playlist name was updated, false otherwise
	 */
	public static boolean renamePlaylist(int playlistNum, long memberId, String playlistName) {
		try (
			Connection connection = DatabaseManager.getConnection();

			PreparedStatement playlistStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT playlists.playlist_id, playlists.playlist_name " +
					"FROM playlists " +
					"INNER JOIN members ON playlists.member_id=members.member_id " +
					"WHERE members.member_long=?");

			PreparedStatement updateStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"UPDATE playlists SET playlist_name=? " +
				"WHERE member_id=" +
					"(SELECT members.member_id FROM members WHERE members.member_long=?) " +
				"AND playlist_id=?")
			) {

			playlistStatement.setString(1, String.valueOf(memberId));

			List<String> playlistIds = new ArrayList<>();
			List<String> playlistNames = new ArrayList<>();
			try (ResultSet resultSet = playlistStatement.executeQuery()) {
				while (resultSet.next()) {
					playlistIds.add(resultSet.getString("playlist_id"));
					playlistNames.add(resultSet.getString("playlist_name"));
				}
			}

			if (playlistIds.size() != playlistNames.size()) {
				logger.error("Could not retrieve song links from playlist id");
				return false;
			}

			if (playlistNum > playlistIds.size()) {
				logger.error("Playlist num does not exist in user's playlists");
				return false;
			}

			int playlistId = 0;
			try {
				playlistId = Integer.parseInt(playlistIds.get(playlistNum - 1));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				logger.error("Could not retrieve playlist id from array");
			}

			if (playlistId == 0)
				return false;

			updateStatement.setString(1, playlistName);
			updateStatement.setString(2, String.valueOf(memberId));
			updateStatement.setString(3, String.valueOf(playlistId));
			updateStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not rename the playlist given playlist num and member id");
			return false;
		}

		return true;
	}

	/**
	 * Retrieves playlist information of a user
	 * @param memberId The member id to get playlist information from
	 * @return The list of PlaylistInfo retrieved from the database
	 */
	public static List<PlaylistInfo> getMemberPlaylistInfo(long memberId) {
		try (
			Connection connection = DatabaseManager.getConnection();

			PreparedStatement playlistStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT playlists.song_count, playlists.playlist_name " +
					"FROM playlists " +
					"INNER JOIN members ON playlists.member_id=members.member_id " +
					"WHERE members.member_long=?")
		) {

			playlistStatement.setString(1, String.valueOf(memberId));

			List<PlaylistInfo> playlistInfoList = new ArrayList<>();
			try (ResultSet resultSet = playlistStatement.executeQuery()) {
				int songCount = 0;
				while (resultSet.next()) {
					try {
						songCount = Integer.parseInt(resultSet.getString("song_count"));
					} catch (NumberFormatException e) {
						logger.error("Couldn't get current playlist song count");
					}

					playlistInfoList.add(
						new PlaylistInfo(
							resultSet.getString("playlist_name"),
							songCount,
							new ArrayList<>(),
							memberId
						)
					);
				}
			}

			return playlistInfoList;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not retrieve member `" + memberId + "` playlist information");
			return new ArrayList<>();
		}
	}
}