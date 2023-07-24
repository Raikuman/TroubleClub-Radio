package com.raikuman.troubleclub.radio.config.playlist;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.database.DefaultDatabaseHandler;
import com.raikuman.troubleclub.radio.music.PlaylistInfo;
import kotlin.Triple;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * Handles getting and updating values of the playlist tables in the database
 *
 * @version 1.4 2023-05-07
 * @since 1.2
 */
public class PlaylistDB {

	private static final Logger logger = LoggerFactory.getLogger(PlaylistDB.class);

	/**
	 * Handles creating a playlist in the database
	 * @param playlistInfo The PlaylistInfo to add to the database
	 * @return Whether the playlist was successfully created
	 */
	public static boolean createPlaylist(PlaylistInfo playlistInfo, User user) {
		// Add songs to song table
		List<Integer> songIds = new ArrayList<>();
		for (String link : playlistInfo.getSongs()) {
			int songId = addSongToDatabase(link);
			if (songId == -1) continue;

			songIds.add(songId);
		}

		// Add playlist to user_playlist table
		int playlistId = addPlaylistToDatabase(DefaultDatabaseHandler.getUserId(user),
			playlistInfo.getName(), playlistInfo.getSongs().size());
		if (playlistId == -1) {
			logger.error("Could not create playlist for " + user.getEffectiveName() + ":" + user.getId());
			return false;
		}

		// Add playlist and songs to playlist_song table
		for (int i = 0; i < songIds.size(); i++) {
			addPlaylistAndSongToDatabase(playlistId, songIds.get(i), i + 1);
		}

		return true;
	}

	/**
	 * Handles deleting a playlist from the database
	 * @param playlistId The playlist id to delete from the database
	 * @return Whether the playlist was successfully deleted
	 */
	public static boolean deletePlaylist(int playlistId) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"DELETE FROM user_playlist WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not delete playlist id " + playlistId);
			return false;
		}

		cleanUnusedSongs();

		return true;
	}

	/**
	 * Handles renaming a playlist from the database
	 * @param playlistId The playlist id to rename from the database
	 * @param playlistName The new name of the playlist
	 * @return Whether the playlist was successfully renamed
	 */
	public static boolean renamePlaylist(int playlistId, String playlistName) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"UPDATE user_playlist SET playlist_name = ? WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, playlistName);
			preparedStatement.setString(2, String.valueOf(playlistId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not rename playlist with id " + playlistId);
			return false;
		}

		return true;
	}

	public static List<Triple<String, Integer, Integer>> getBasicPlaylistInfo(User user) {
		return getBasicPlaylistInfoFromDatabase(DefaultDatabaseHandler.getUserId(user));
	}

	public static PlaylistInfo getUserPlaylist(User user, int playlistNum) {
		int userId = DefaultDatabaseHandler.getUserId(user);

		return getPlaylistFromDatabase(getBasicPlaylistInfoFromDatabase(userId).get(playlistNum));
	}

	public static boolean addSongToPlaylist(int playlistId, String songUrl, int songNum) {
		int songId = addSongToDatabase(songUrl);
		if (songId == -1) {
			return false;
		}

		return addSongToPlaylistInDatabase(playlistId, songId, songNum);
	}

	public static String removeSongFromPlaylist(int playlistId, int songNum) {
		return removeSongFromPlaylistDatabase(playlistId, songNum);
	}

	private static String removeSongFromPlaylistDatabase(int playlistId, int trackNum) {
		// Retrieve the songId and songNum of the given track num in the playlist
		int count = 1, songId = -1, songNum = -1;
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT song_id, song_num FROM playlist_song WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					if (count == trackNum) {
						songId = resultSet.getInt(1);
						songNum = resultSet.getInt(2);
					}
					count++;
				}
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve song id from song num " + trackNum + " in playlist id " + playlistId);
		}

		if (songId == -1 || songNum == -1) {
			return "";
		}

		// Remove the song id from the playlist
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"DELETE FROM playlist_song WHERE user_playlist_id = ? AND song_num = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.setString(2, String.valueOf(songNum));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not delete song id " + songId + " from playlist id " + playlistId);
			return "";
		}

		// Update playlist track numbers
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"UPDATE playlist_song SET song_num = song_num - 1 WHERE user_playlist_id = ? AND song_num > ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.setString(2, String.valueOf(songNum));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not update song num with playlist id " + playlistId + " and song num " + songNum);
			return "";
		}

		// Decrement number of songs in user_playlist
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"UPDATE user_playlist SET song_num = song_num - 1 WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not update user playlist song num in playlist id " + playlistId);
			return "";
		}

		// Get song link and return
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT song_link FROM song WHERE song_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(songId));
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getString(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve song link with song id " + songId);
		}

		return "";
	}

	private static boolean addSongToPlaylistInDatabase(int playlistId, int songId, int songNum) {
		// Check if songNum is correct number
		int numberOfSongs = -1;
		if (songNum != -1) {
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"SELECT song_num FROM user_playlist WHERE user_playlist_id = ?"
				)) {
				preparedStatement.setString(1, String.valueOf(playlistId));
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					numberOfSongs = resultSet.getInt(1);
				}
			} catch (SQLException e) {
				logger.error("Could not retrieve song ids with playlist id " + playlistId);
			}

			if (numberOfSongs == -1) {
				return false;
			}

			if (songNum > numberOfSongs) {
				return false;
			}
		}

		if (songNum == -1) {
			// Add song to end of playlist

			// Get highest song num in playlist
			int highestSongNum = -1;
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"SELECT song_num FROM playlist_song WHERE user_playlist_id = ?"
				)) {
				preparedStatement.setString(1, String.valueOf(playlistId));
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						int currentSongNum = resultSet.getInt(1);
						if (currentSongNum > highestSongNum) {
							highestSongNum = currentSongNum;
						}
					}
				}
			} catch (SQLException e) {
				logger.error("Could not retrieve song ids with playlist id " + playlistId);
			}

			// Add song to playlist with next highest song num
			if (highestSongNum == -1) {
				return false;
			}

			addPlaylistAndSongToDatabase(playlistId, songId, highestSongNum + 1);

		} else {
			// Add song to target number and push all song numbers after by 1
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"UPDATE playlist_song SET song_num = song_num + 1 WHERE user_playlist_id = ? AND song_num >= ?"
				)) {
				preparedStatement.setString(1, String.valueOf(playlistId));
				preparedStatement.setString(2, String.valueOf(songNum));
				preparedStatement.execute();
			} catch (SQLException e) {
				logger.error("Could not update song num with playlist id " + playlistId + " and song num " + songNum);
			}

			// Finally add song to playlist
			addPlaylistAndSongToDatabase(playlistId, songId, songNum);
		}

		// Increment number of songs in user_playlist
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"UPDATE user_playlist SET song_num = song_num + 1 WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not update user playlist song num in playlist id " + playlistId);
			return false;
		}

		return true;
	}

	private static PlaylistInfo getPlaylistFromDatabase(Triple<String, Integer, Integer> basicInfo) {
		// Retrieve song ids
		List<Integer> songIds = new ArrayList<>();
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT song_id FROM playlist_song WHERE user_playlist_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(basicInfo.getThird()));
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					songIds.add(resultSet.getInt(1));
				}
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve song ids with playlist id " + basicInfo.getThird());
		}

		if (songIds.isEmpty()) return null;

		// Retrieve song links to populate PlaylistInfo
		List<String> songLinks = new ArrayList<>();
		for (Integer songId : songIds) {
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"SELECT song_link FROM song WHERE song_id = ?"
				)) {
				preparedStatement.setString(1, String.valueOf(songId));
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						songLinks.add(resultSet.getString(1));
					}
				}
			} catch (SQLException e) {
				logger.error("Could not retrieve song link with song id " + songId);
			}
		}

		if (songLinks.isEmpty()) return null;

		return new PlaylistInfo(basicInfo.getFirst(), songLinks);
	}

	/**
	 * Gets playlist information for a user from the database user_playlist table
	 * @param userId The user to get playlist information
	 * @return The list of playlist information pairs
	 */
	private static List<Triple<String, Integer, Integer>> getBasicPlaylistInfoFromDatabase(int userId) {
		List<Triple<String, Integer, Integer>> playlists = new ArrayList<>();
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT playlist_name, song_num, user_playlist_id FROM user_playlist WHERE user_id = ?"
			)) {
			preparedStatement.setString(1, String.valueOf(userId));
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					playlists.add(new Triple<>(resultSet.getString(1), resultSet.getInt(2), resultSet.getInt(3)));
				}
			}
		} catch (SQLException e) {
			logger.error("Could not retrieve playlist with user id " + userId);
		}

		return playlists;
	}

	/**
	 * Add a playlist to the database user_playlist table
	 * @param userId The user to add
	 * @param playlistName The name of the playlist to add
	 * @return The user_playlist_id of the added playlist
	 */
	private static int addPlaylistToDatabase(int userId, String playlistName, int numSongs) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"INSERT INTO user_playlist(user_id, playlist_name, song_num) VALUES(?, ?, ?)"
			)) {
			preparedStatement.setString(1, String.valueOf(userId));
			preparedStatement.setString(2, playlistName);
			preparedStatement.setString(3, String.valueOf(numSongs));
			preparedStatement.execute();
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Could not add playlist with user id " + userId + " and name " + playlistName);
		}

		return -1;
	}

	/**
	 * Add a playlist and song to the database playlist_song table
	 * @param playlistId The playlist to add
	 * @param songId The song to add
	 * @param songNum The song num to add
	 */
	private static void addPlaylistAndSongToDatabase(int playlistId, int songId, int songNum) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"INSERT INTO playlist_song(user_playlist_id, song_id, song_num) VALUES(?, ?, ?)"
			)) {
			preparedStatement.setString(1, String.valueOf(playlistId));
			preparedStatement.setString(2, String.valueOf(songId));
			preparedStatement.setString(3, String.valueOf(songNum));
			preparedStatement.execute();
		} catch (SQLException e) {
			logger.error("Could not add song id " + songId + " for playlist id " + playlistId);
		}
	}

	/**
	 * Add a song to the database song table
	 * @param link The song link
	 * @return The song_id of the added song
	 */
	private static int addSongToDatabase(String link) {
		int songId = getSongId(link);

		if (songId != -1) return songId;

		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"INSERT INTO song(song_link) VALUES(?)"
			)) {
			preparedStatement.setString(1, link);
			preparedStatement.execute();
			try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Could not add song with link " + link);
			e.printStackTrace();
		}

		return -1;
	}

	/**
	 * Get the song_id from the database song table
	 * @param link The link of the song to get the id from
	 * @return The song_id
	 */
	private static int getSongId(String link) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT song_id FROM song WHERE song_link = ?"
			)) {
			preparedStatement.setString(1, link);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("Could not get song id with link " + link);
		}

		return -1;
	}

	/**
	 * Removes all songs from the song table that are not in a playlist
	 */
	public static void cleanUnusedSongs() {
		// Get all ids from song table
		List<Integer> songIds = new ArrayList<>();
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQL
				"SELECT song_id FROM song"
			)) {
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					songIds.add(resultSet.getInt(1));
				}
			}
		} catch (SQLException e) {
			logger.error("Could not get song ids");
		}

		// Check if song exists in a playlist. If not, add to deletion list
		List<Integer> deleteSongIds = new ArrayList<>();
		for (Integer songId : songIds) {
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"SELECT playlist_song_id FROM playlist_song WHERE song_id = ?"
				)) {
				preparedStatement.setString(1, String.valueOf(songId));

				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (!resultSet.next()) {
						if (resultSet.getInt(1) < 1) {
							deleteSongIds.add(songId);
						}
					}
				}
			} catch (SQLException e) {
				logger.error("Could not check if song id " + songId + " is in a playlist");
			}
		}

		// Delete all extra songs
		for (Integer songId : deleteSongIds) {
			try (
				Connection connection = DatabaseManager.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(
					// language=SQL
					"DELETE FROM song WHERE song_id = ?"
				)) {
				preparedStatement.setString(1, String.valueOf(songId));
				preparedStatement.execute();
			} catch (SQLException e) {
				logger.error("Could not remove song id " + songId);
			}
		}
	}
}