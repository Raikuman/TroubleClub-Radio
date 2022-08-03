package com.raikuman.troubleclub.radio.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Schedules tracks to play from a queue
 *
 * @version 1.7 2022-03-08
 * @since 1.0
 */
public class TrackScheduler extends AudioEventAdapter {

	public final AudioPlayer audioPlayer;
	public final BlockingQueue<AudioTrack> queue;
	public boolean repeat = false;
	public boolean repeatQueue = false;

	public TrackScheduler(AudioPlayer audioPlayer) {
		this.audioPlayer = audioPlayer;
		this.queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack audioTrack, AudioTrackEndReason endReason) {
		if (!endReason.mayStartNext)
			return;

		if (repeat) {
			this.audioPlayer.startTrack(audioTrack.makeClone(), true);
			return;
		}

		if (repeatQueue)
			this.queue.offer(audioTrack.makeClone());

		nextTrack();
	}

	/**
	 * Adds a track to the end of the queue
	 * @param audioTrack The track to add
	 */
	public void queue(AudioTrack audioTrack) {
		if (!this.audioPlayer.startTrack(audioTrack, true)) {
			this.queue.offer(audioTrack);
		}
	}

	/**
	 * Adds a track to the top of the queue
	 * @param audioTrack The track to add
	 */
	public void addToTop(AudioTrack audioTrack) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		this.queue.offer(audioTrack);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);
	}

	/**
	 * Moves a track from the queue to the top of the queue
	 * @param trackNum The track number to move
	 * @return The track information of the moved track
	 */
	public AudioTrackInfo moveTrack(int trackNum) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		AudioTrack audioTrack = queueTracks.remove(trackNum - 1);

		this.queue.offer(audioTrack);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);

		return audioTrack.getInfo();
	}

	/**
	 * Moves a track from the queue to a specified location in the queue
	 * @param trackNum The track number to move
	 * @param position The position to move the track to
	 * @return The track information of the moved track
	 */
	public AudioTrackInfo moveTrack(int trackNum, int position) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		AudioTrack audioTrack = queueTracks.remove(trackNum - 1);

		int currentPos = 1;
		for (AudioTrack track : queueTracks) {
			if (currentPos == position)
				this.queue.offer(audioTrack);

			this.queue.offer(track);
			currentPos++;
		}

		return audioTrack.getInfo();
	}

	/**
	 * Plays the next track in the queue
	 */
	public void nextTrack() {
		this.audioPlayer.startTrack(this.queue.poll(), false);
	}

	/**
	 * Removes all duplicate songs from the queue
	 * @return The number of tracks removed from the queue
	 */
	public int pruneTracks() {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		List<String> identifiers = new ArrayList<>();
		for (AudioTrack track : queueTracks)
			identifiers.add(track.getIdentifier());

		identifiers = new ArrayList<>(new LinkedHashSet<>(identifiers));

		List<AudioTrack> prunedTracks = new ArrayList<>();
		for (AudioTrack track : queueTracks) {
			if (identifiers.contains(track.getIdentifier())) {
				prunedTracks.add(track);
				identifiers.remove(track.getIdentifier());
			}
		}

		for (AudioTrack track : prunedTracks)
			this.queue.offer(track);

		return queueTracks.size() - prunedTracks.size();
	}

	/**
	 * Removes a track from the queue at the specified location in the queue
	 * @param trackNum The track number to move
	 */
	public AudioTrackInfo removeTrack(int trackNum) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		AudioTrack audioTrack = queueTracks.remove(trackNum - 1);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);

		return audioTrack.getInfo();
	}

	/**
	 * Rewinds the current playing track to the beginning
	 */
	public void rewind() {
		AudioTrack playingTrack = this.audioPlayer.getPlayingTrack();
		if (playingTrack == null)
			return;

		this.audioPlayer.startTrack(playingTrack.makeClone(), false);
	}

	/**
	 * Skips to the specified location in the queue, playing the track it was skipped to
	 * @param position The track number to skip to
	 * @return The track information of the first track skipped to
	 */
	public AudioTrackInfo skipTo(int position) {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		for (int i = position - 1; i < queueTracks.size(); i++)
			this.queue.offer(queueTracks.get(i));

		nextTrack();

		return queueTracks.get(position - 1).getInfo();
	}

	/**
	 * Shuffles the current queue
	 */
	public int shuffle() {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		Collections.shuffle(queueTracks);
		for (AudioTrack track : queueTracks)
			this.queue.offer(track);

		return queueTracks.size();
	}

	/**
	 * Randomly gets a track from the queue and plays it immediately
	 * @return The track information of the random track
	 */
	public AudioTrackInfo random() {
		List<AudioTrack> queueTracks = new ArrayList<>();
		this.queue.drainTo(queueTracks);

		int position = new Random().nextInt(queueTracks.size());
		AudioTrack audioTrack = queueTracks.remove(position);

		for (AudioTrack track : queueTracks)
			this.queue.offer(track);

		this.audioPlayer.startTrack(audioTrack, false);

		return audioTrack.getInfo();
	}
}
