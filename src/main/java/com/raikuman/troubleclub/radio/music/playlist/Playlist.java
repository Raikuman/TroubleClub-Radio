package com.raikuman.troubleclub.radio.music.playlist;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private final String title, url;
    private final int id, songs;
    private final User user;

    public Playlist(String title, String url, int songs, User user) {
        this.id = -1;
        this.title = title;
        this.url = url;
        this.songs = songs;
        this.user = user;
    }

    public Playlist(int id, String title, String url, int songs, User user) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.songs = songs;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getSongs() {
        return songs;
    }

    public User getUser() {
        return user;
    }
}
