package com.raikuman.troubleclub.radio.music.playlist;

import net.dv8tion.jda.api.entities.User;

public class Playlist {

    private final String title;
    private final int id, songs;
    private final User user;

    public Playlist(String title, int songs, User user) {
        this.id = -1;
        this.title = title;
        this.songs = songs;
        this.user = user;
    }

    public Playlist(int id, String title, int songs, User user) {
        this.id = id;
        this.title = title;
        this.songs = songs;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getNumSongs() {
        return songs;
    }

    public User getUser() {
        return user;
    }
}
