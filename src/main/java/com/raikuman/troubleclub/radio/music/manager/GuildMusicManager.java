package com.raikuman.troubleclub.radio.music.manager;

import com.raikuman.troubleclub.radio.database.music.MusicDatabaseHandler;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {

    private final AudioPlayer[] audioPlayers;
    private final TrackScheduler[] trackSchedulers;
    private final MixingSendHandler mixingSendHandler;
    private static final int MAX_AUDIO_PLAYERS = 3;
    private int currentAudioPlayer;

    public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
        this.currentAudioPlayer = 1;
        this.audioPlayers = new AudioPlayer[MAX_AUDIO_PLAYERS];
        this.trackSchedulers = new TrackScheduler[MAX_AUDIO_PLAYERS];

        playerManager.getConfiguration().setOutputFormat(new Pcm16AudioDataFormat(2, 48000, 960, true));

        // Handle audio players
        for (int i = 0; i < MAX_AUDIO_PLAYERS; i++) {
            // Create audio players
            audioPlayers[i] = playerManager.createPlayer();
            trackSchedulers[i] = new TrackScheduler(audioPlayers[i]);
            audioPlayers[i].addListener(trackSchedulers[i]);

            // Load volume from database
            audioPlayers[i].setVolume(MusicDatabaseHandler.getVolume(guild, i + 1));

            // Equalize filter
            audioPlayers[i].setFilterFactory(new EqualizerFactory());
        }

        // Handle track mixing
        this.mixingSendHandler = new MixingSendHandler(audioPlayers);
    }

    public MixingSendHandler getMixingSendHandler() {
        return mixingSendHandler;
    }

    public AudioPlayer getAudioPlayer(int audioPlayerNum) {
        return audioPlayers[audioPlayerNum - 1];
    }

    public void setCurrentAudioPlayer(int currentAudioPlayer) {
        if ((currentAudioPlayer < 1) || (currentAudioPlayer > MAX_AUDIO_PLAYERS))
            return;

        this.currentAudioPlayer = currentAudioPlayer;
    }

    public AudioPlayer getCurrentAudioPlayer() {
        return audioPlayers[currentAudioPlayer - 1];
    }

    public int getCurrentAudioPlayerNum() {
        return currentAudioPlayer;
    }

    public TrackScheduler getTrackScheduler() {
        return trackSchedulers[currentAudioPlayer - 1];
    }
}
