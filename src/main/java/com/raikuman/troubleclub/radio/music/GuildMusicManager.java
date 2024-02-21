package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager {

    private final AudioPlayer[] audioPlayers;
    private final TrackScheduler[] trackSchedulers;
    private final MixingSendHandler mixingSendHandler;
    private static final int MAX_AUDIO_PLAYERS = 3;
    private int currentTrack;

    public GuildMusicManager(AudioPlayerManager playerManager) {
        this.currentTrack = 1;
        this.audioPlayers = new AudioPlayer[MAX_AUDIO_PLAYERS];
        this.trackSchedulers = new TrackScheduler[MAX_AUDIO_PLAYERS];

        playerManager.getConfiguration().setOutputFormat(new Pcm16AudioDataFormat(2, 48000, 960, true));

        for (int i = 0; i < MAX_AUDIO_PLAYERS; i++) {
            // Create audio players
            audioPlayers[i] = playerManager.createPlayer();
            trackSchedulers[i] = new TrackScheduler(audioPlayers[i]);
            audioPlayers[i].addListener(trackSchedulers[i]);

            // Default volume
            audioPlayers[i].setVolume(25);

            // Equalize filter
            audioPlayers[i].setFilterFactory(new EqualizerFactory());
        }

        // Handle track mixing
        this.mixingSendHandler = new MixingSendHandler(audioPlayers);
    }

    public MixingSendHandler getMixingSendHandler() {
        return mixingSendHandler;
    }

    public int getCurrentTrack() {
        return currentTrack;
    }

    public void setCurrentTrack(int currentTrack) {
        if ((currentTrack < 1) || (currentTrack > MAX_AUDIO_PLAYERS))
            return;

        this.currentTrack = currentTrack;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayers[currentTrack - 1];
    }

    public TrackScheduler getTrackScheduler() {
        return trackSchedulers[currentTrack - 1];
    }
}
