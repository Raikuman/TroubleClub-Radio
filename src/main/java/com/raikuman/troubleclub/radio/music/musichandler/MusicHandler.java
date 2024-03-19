package com.raikuman.troubleclub.radio.music.musichandler;

import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public abstract class MusicHandler {

    private final String url;
    private final MessageChannelUnion messageChannel;
    private final User user;
    private final Message message;
    private final Guild guild;

    public MusicHandler(Guild guild, MessageChannelUnion channel, Message message, User user, String url) {
        this.url = url;

        this.messageChannel = channel;
        this.user = user;
        this.message = message;
        this.guild = guild;
    }

    public String getUrl() {
        return url;
    }

    public MessageChannelUnion getMessageChannel() {
        return messageChannel;
    }

    public User getUser() {
        return user;
    }

    public Message getMessage() {
        return message;
    }

    public Guild getGuild() {
        return guild;
    }

    public abstract AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager);

    public AudioLoadResultHandler getResultHandler(GuildMusicManager musicManager, Playlist playlist) {
        return null;
    }
}
