package com.raikuman.troubleclub.radio.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private final String title, url;
    private final int songs;
    private final User user;
    private final List<AudioTrack> tracks;

    public Playlist(String title, String url, int songs, User user, List<AudioTrack> tracks) {
        this.title = title;
        this.url = url;
        this.songs = songs;
        this.user = user;
        this.tracks = tracks;
    }

    public Playlist(String title, String url, int songs, User user) {
        this.title = title;
        this.url = url;
        this.songs = songs;
        this.user = user;
        this.tracks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public User getUser() {
        return user;
    }

    public List<AudioTrack> getTracks() {
        return tracks;
    }

    public void addTracks(List<AudioTrack> tracks) {
        this.tracks.addAll(tracks);
    }
}
