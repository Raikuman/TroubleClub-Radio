package com.raikuman.troubleclub.radio.music.manager;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);
    public final AudioPlayer audioPlayer;
    public final BlockingQueue<AudioTrack> queue;
    private boolean repeat = false, repeatQueue = false;

    public TrackScheduler(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
        this.queue = new LinkedBlockingQueue<>();
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isRepeatQueue() {
        return repeatQueue;
    }

    public void setRepeatQueue(boolean repeatQueue) {
        this.repeatQueue = repeatQueue;
    }

    public void queue(AudioTrack track) {
        if (!this.audioPlayer.startTrack(track, true)) {
            if (!this.queue.offer(track)) {
                logger.error("Could not add audio track to queue: " + track.getInfo().title);
            }
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (!endReason.mayStartNext) return;

        // Handle repeating current track
        if (repeat) {
            player.startTrack(track.makeClone(), true);
            return;
        }

        // Handle repeating queue by adding current track to end of queue
        if (repeatQueue) {
            if (!this.queue.offer(track.makeClone())) {
                logger.error("Could not add audio track to queue (repeating queue): " + track.getInfo().title);
            }
        }

        nextTrack();
    }

    public void nextTrack() {
        this.audioPlayer.startTrack(this.queue.poll(), false);
    }
}
