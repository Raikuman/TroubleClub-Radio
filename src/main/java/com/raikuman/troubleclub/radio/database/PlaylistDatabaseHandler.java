package com.raikuman.troubleclub.radio.database;

import com.raikuman.botutilities.database.DatabaseManager;
import com.raikuman.botutilities.defaults.database.DefaultDatabaseHandler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class PlaylistDatabaseHandler {

    public static final Logger logger = LoggerFactory.getLogger(PlaylistDatabaseHandler.class);

    public static boolean addPlaylist(Playlist playlist, List<String> tracksEncoded) {
        int userId = DefaultDatabaseHandler.getUserId(playlist.getUser());

        // Couldn't retrieve user id
        if (userId == -1) {
            return false;
        }

        // Add playlist to playlist database
        int playlistId = -1;
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO playlist(user_id, name, songs) VALUES(?, ?, ?)"
            )) {
            statement.setInt(1, userId);
            statement.setString(2, playlist.getTitle());
            statement.setInt(3, playlist.getNumSongs());
            statement.execute();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    playlistId =  resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred adding playlist to playlist table for: {}", playlist.getTitle());
            return false;
        }

        // Couldn't add playlist
        if (playlistId == -1) {
            return false;
        }

        // Construct playlist_song database with input playlist
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO playlist_song(playlist_id, song_id) VALUES(?, ?)"
            )) {
            for (int songId : addTracks(tracksEncoded)) {
                statement.setInt(1, playlistId);
                statement.setInt(2, songId);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            logger.error("An error occurred adding playlist id and song ids to playlist_song table for: {}", playlist.getTitle());
            return false;
        }

        return true;
    }

    public static int numberOfPlaylists(User user) {
        int userId = DefaultDatabaseHandler.getUserId(user);

        // Couldn't retrieve user id
        if (userId == -1) {
            return 0;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM playlist WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving number of playlists for user: {}", user.getEffectiveName());
        }

        return 0;
    }

    public static Playlist getPlaylist(User user, int playlistId) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT name, songs FROM playlist WHERE playlist_id = ?"
            )) {
            statement.setInt(1, playlistId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Playlist(
                        playlistId,
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        user);
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving playlist with id: {}", playlistId);
        }

        return null;
    }

    public static List<Playlist> getPlaylists(User user) {
        int userId = DefaultDatabaseHandler.getUserId(user);

        // Couldn't retrieve user id
        if (userId == -1) {
            return null;
        }

        // Retrieve all user's playlists
        List<Playlist> playlists = new ArrayList<>();
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT playlist_id, name, songs FROM playlist WHERE user_id = ?"
            )) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    playlists.add(new Playlist(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        user
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving playlists for user: {}", user.getEffectiveName());
            return null;
        }

        return playlists;
    }

    public static List<String> getPlaylistTracks(int playlistId) {
        // Get song ids for playlist
        List<Integer> songIds = new ArrayList<>();
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT song_id FROM playlist_song WHERE playlist_id = ?"
            )) {
            statement.setInt(1, playlistId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    songIds.add(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving song ids for playlist: {}", playlistId);
            return null;
        }

        if (songIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Get song urls from song ids
        List<String> tracksEncoded = new ArrayList<>();
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT encoded FROM song WHERE song_id = ?"
            )) {
            for (Integer songId : songIds) {
                statement.setInt(1, songId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        tracksEncoded.add(resultSet.getString(1));
                    } else {
                        logger.error("An error occurred retrieving song url for song id: {}", songId);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred retrieving song urls for playlist: {}", playlistId);
            return null;
        }

        return tracksEncoded;
    }

    public static boolean renamePlaylist(int playlistId, String newName) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "UPDATE playlist SET name = ? WHERE playlist_id = ?"
            )) {
            statement.setString(1, newName);
            statement.setInt(2, playlistId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred renaming playlist: {} with name: {}", playlistId, newName);
            return false;
        }

        return true;
    }

    public static boolean deletePlaylist(int playlistId) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM playlist WHERE playlist_id = ?"
            )) {
            statement.setInt(1, playlistId);
            statement.execute();
        } catch (SQLException e) {
            logger.error("An error occurred removing playlist from playlist table for: {}", playlistId);
            return false;
        }

        return true;
    }

    private static int[] addTracks(List<String> tracksEncoded) {
        int[] trackIds = new int[tracksEncoded.size()];
        for (int i = 0; i < trackIds.length; i++) {
            trackIds[i] = addTrack(tracksEncoded.get(i));
        }

        return trackIds;
    }

    private static int addTrack(String trackEncoded) {
        int trackId = getTrackId(trackEncoded);

        // Return track id if already found
        if (trackId != -1) {
            return trackId;
        }

        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO song(encoded) VALUES(?)"
            )) {
            statement.setString(1, trackEncoded);
            statement.execute();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred adding track id for: {}", trackEncoded);
            return -1;
        }
    }

    private static int getTrackId(String trackEncoded) {
        try (
            Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                "SELECT song_id FROM song WHERE encoded = ?"
            )) {
            statement.setString(1, trackEncoded);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            logger.error("An error occurred getting track id for: {}", trackEncoded);
            return -1;
        }
    }
}
