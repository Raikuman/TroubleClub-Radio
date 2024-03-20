package com.raikuman.troubleclub.radio.music.playerhandler.music;

import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class MusicHandler {

    private final MessageReceivedEvent event;
    private final String url;
    private final GuildMusicManager musicManager;

    public MusicHandler(MessageReceivedEvent event, String url) {
        this.event = event;
        this.url = url;

        this.musicManager = MusicManager.getInstance().getMusicManager(event.getGuild());
    }

    public String getUrl() {
        return url;
    }

    public MessageChannelUnion getChannel() {
        return event.getChannel();
    }

    public User getUser() {
        return event.getAuthor();
    }

    public Message getMessage() {
        return event.getMessage();
    }

    public Guild getGuild() {
        return event.getGuild();
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public GuildMusicManager getMusicManager() {
        return musicManager;
    }

    public abstract AudioLoadResultHandler getResultHandler();
}
