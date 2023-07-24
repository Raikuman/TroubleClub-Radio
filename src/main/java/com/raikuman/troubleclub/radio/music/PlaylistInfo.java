package com.raikuman.troubleclub.radio.music;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an object to move around playlist information
 *
 * @version 1.3 2023-29-06
 * @since 1.2
 */
public class PlaylistInfo {

	private final String name;
	private final List<String> songs;
	private final String playlistLink;

	public PlaylistInfo(String name, List<String> songs) {
		this.name = name;
		this.songs = songs;
		this.playlistLink = "";
	}

	public PlaylistInfo(String name, String playlistLink) {
		this.name = name;
		this.songs = new ArrayList<>();
		this.playlistLink = playlistLink;
	}

	public PlaylistInfo(String name, List<String> songs, String playlistLink) {
		this.name = name;
		this.songs = songs;
		this.playlistLink = playlistLink;
	}

	public String getName() {
		if (name.isEmpty())
			return "Unnamed Cassette";
		else
			return name;
	}

	public List<String> getSongs() {
		return songs;
	}

	public String getPlaylistLink() { return playlistLink; }
}
