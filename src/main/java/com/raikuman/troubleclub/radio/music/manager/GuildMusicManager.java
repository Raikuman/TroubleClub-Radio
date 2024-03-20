package com.raikuman.troubleclub.radio.music.manager;

import com.raikuman.troubleclub.radio.database.MusicDatabaseHandler;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.Pcm16AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageInput;
import com.sedmelluq.discord.lavaplayer.tools.io.MessageOutput;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Base64;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public class GuildMusicManager {

    private static final Logger logger = LoggerFactory.getLogger(GuildMusicManager.class);
    private final AudioPlayer[] audioPlayers;
    private final TrackScheduler[] trackSchedulers;
    private final MixingSendHandler mixingSendHandler;
    private final AudioPlayerManager playerManager;
    private int currentAudioPlayer;
    public static final int MAX_AUDIO_PLAYERS = 3;
    public static final double REDUCE_VOLUME = 5.0;


    public GuildMusicManager(AudioPlayerManager playerManager, Guild guild) {
        this.playerManager = playerManager;
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
            audioPlayers[i].setVolume((int) Math.ceil(MusicDatabaseHandler.getVolume(guild, i + 1) / REDUCE_VOLUME));

            // Equalize filter
            audioPlayers[i].setFilterFactory(new EqualizerFactory());
        }

        // Handle track mixing
        this.mixingSendHandler = new MixingSendHandler(audioPlayers);
    }

    public MixingSendHandler getMixingSendHandler() {
        return mixingSendHandler;
    }

    public TrackScheduler getTrackScheduler(int trackSchedulerNum) {
        return trackSchedulers[trackSchedulerNum - 1];
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

    public TrackScheduler getCurrentTrackScheduler() {
        return trackSchedulers[currentAudioPlayer - 1];
    }

    public String encodeTrack(AudioTrack audioTrack) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // Encode track to output stream
            playerManager.encodeTrack(new MessageOutput(outputStream), audioTrack);

            // Compress stream
            ByteArrayOutputStream compressStream = new ByteArrayOutputStream();
            DeflaterOutputStream deflateStream = new DeflaterOutputStream(compressStream);
            deflateStream.write(outputStream.toByteArray());
            deflateStream.flush();
            deflateStream.close();

            return Base64.getEncoder().encodeToString(compressStream.toByteArray());
        } catch (IOException e) {
            logger.error("Could not encode track: {}", audioTrack.getInfo().uri);
            return null;
        }
    }

    public AudioTrack decodeTrack(String base64) {
        try {
            // Decompress encoded track
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InflaterOutputStream inflateStream = new InflaterOutputStream(outputStream);
            inflateStream.write(Base64.getDecoder().decode(base64));
            inflateStream.flush();
            inflateStream.close();

            // Decode track to retrieve AudioTrack
            return playerManager.decodeTrack(new MessageInput(new ByteArrayInputStream(outputStream.toByteArray()))).decodedTrack;
        } catch (IOException e) {
            logger.error("Could not decode track: {}", base64);
            return null;
        }
    }
}
