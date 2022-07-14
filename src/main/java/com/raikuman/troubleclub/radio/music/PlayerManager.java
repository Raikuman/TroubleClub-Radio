package com.raikuman.troubleclub.radio.music;

import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.config.MusicDB;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles loading and playing tracks for the guild music manager
 *
 * @version 1.6 2022-13-07
 * @since 1.0
 */
public class PlayerManager {

	private static PlayerManager PLAYER_INSTANCE;
	private final Map<Long, GuildMusicManager> musicManagerMap;
	private final AudioPlayerManager audioPlayerManager;

	public PlayerManager() {
		this.musicManagerMap = new HashMap<>();
		this.audioPlayerManager = new DefaultAudioPlayerManager();

		// Check source of track
		AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
		AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
	}

	/**
	 * Returns the music manager from a guild
	 * @param guild The guild to get the music manager from
	 * @return The music manager
	 */
	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagerMap.computeIfAbsent(guild.getIdLong(), guildId -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.mixingHandler);
			return guildMusicManager;
		});
	}

	/**
	 * Handles normally loading tracks from the queue and playing tracks
	 * @param channel The channel to send messages to
	 * @param trackUrl The track url to get the audio track from
	 * @param user The user to display on an embed
	 */
	public void loadAndPlay(TextChannel channel, String trackUrl, User user, long guildId) {
		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		musicManager.getAudioPlayer().setVolume(loadVolume(guildId, musicManager.getCurrentAudioTrack()));

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				musicManager.getTrackScheduler().queue(audioTrack);

				channel.sendMessageEmbeds(
					trackEmbed(
						musicManager.getTrackScheduler().queue.size(),
						audioTrack,
						user,
						musicManager.getCurrentAudioTrack()
					).build()
				).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				List<AudioTrack> tracks = audioPlaylist.getTracks();

				// Handle searching songs
				if (trackUrl.contains("ytsearch:")) {
					AudioTrack firstTrack = audioPlaylist.getTracks().remove(0);
					if (firstTrack == null) {
						MessageResources.timedMessage(
							"Could not retrieve a track",
							channel,
							5
						);
						return;
					}

					musicManager.getTrackScheduler().queue(firstTrack);

					channel.sendMessageEmbeds(
						trackEmbed(
							musicManager.getTrackScheduler().queue.size(),
							firstTrack,
							user,
							musicManager.getCurrentAudioTrack()
						).build()
					).queue();

					return;
				}

				// Queue playlist if provided a link
				for (AudioTrack track : tracks)
					musicManager.getTrackScheduler().queue(track);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("Adding playlist to queue:", null, user.getAvatarUrl())
					.setTitle(audioPlaylist.getName(), tracks.get(0).getInfo().uri)
					.setColor(RandomColor.getRandomColor());
				builder
					.addField("Songs in Playlist", "`" + audioPlaylist.getTracks().size() + "`songs", true)
					.setFooter("Audio track " + musicManager.getCurrentAudioTrack());

				channel.sendMessageEmbeds(builder.build()).queue();
			}

			@Override
			public void noMatches() {
				MessageResources.timedMessage(
					"Nothing found using `" + trackUrl + "`",
					channel,
					5
				);
			}

			@Override
			public void loadFailed(FriendlyException e) {
				MessageResources.timedMessage(
					"Could not load track! `" + e.getMessage() + "`",
					channel,
					5
				);
			}
		});
	}

	/**
	 * Handles loading tracks to the top of the queue
	 * @param channel The channel to send messages to
	 * @param trackUrl The track url to get the audio track from
	 * @param user The user to display on an embed
	 * @param playNow If the top track should be played immediately
	 */
	public void loadToTop(TextChannel channel, String trackUrl, User user, boolean playNow, long guildId) {
		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());
		musicManager.getAudioPlayer().setVolume(loadVolume(guildId, musicManager.getCurrentAudioTrack()));

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				musicManager.getTrackScheduler().addToTop(audioTrack);

				if (playNow)
					musicManager.getTrackScheduler().nextTrack();

				channel.sendMessageEmbeds(
					topEmbed(
						audioTrack,
						user,
						playNow,
						musicManager.getCurrentAudioTrack()
					).build()
				).queue();
			}

			@Override
			public void playlistLoaded(AudioPlaylist audioPlaylist) {
				// Handle searching songs
				if (trackUrl.contains("ytsearch:")) {
					AudioTrack firstTrack = audioPlaylist.getTracks().remove(0);
					if (firstTrack == null) {
						MessageResources.timedMessage(
							"Could not retrieve a track",
							channel,
							5
						);
						return;
					}

					musicManager.getTrackScheduler().addToTop(firstTrack);

					if (playNow)
						musicManager.getTrackScheduler().nextTrack();

					channel.sendMessageEmbeds(
						topEmbed(
							firstTrack,
							user,
							playNow,
							musicManager.getCurrentAudioTrack()
						).build()
					).queue();
				} else {
					MessageResources.timedMessage(
						"Cannot add playlists to the top of the queue",
						channel,
						5
					);
				}
			}

			@Override
			public void noMatches() {
				MessageResources.timedMessage(
					"Nothing found using `" + trackUrl + "`",
					channel,
					5
				);
			}

			@Override
			public void loadFailed(FriendlyException e) {
				MessageResources.timedMessage(
					"Could not load track! `" + e.getMessage() + "`",
					channel,
					5
				);
			}
		});
	}

	/**
	 * Returns the current instance of the player manager
	 * @return The player manager instance
	 */
	public static PlayerManager getInstance() {
		if (PLAYER_INSTANCE == null) {
			PLAYER_INSTANCE = new PlayerManager();
		}

		return PLAYER_INSTANCE;
	}

	/**
	 * Creates an embed using information from the audio player, queue, and the user
	 * @param queueSize The size of the current queue
	 * @param audioTrack The track that was added to the queue
	 * @param user The user to display on an embed
	 * @return The embed builder with track information
	 */
	private EmbedBuilder trackEmbed(int queueSize, AudioTrack audioTrack, User user, int currentAudioTrack) {
		EmbedBuilder builder = new EmbedBuilder()
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor());
		builder
			.addField("Channel", audioTrack.getInfo().author, true)
			.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrack.getDuration()), true);

		String title, nowPlaying;
		if (queueSize == 0) {
			title = "▶️ Playing:";
			nowPlaying = "Now playing";
		} else {
			title = "⏭️ Adding to queue:";
			nowPlaying = String.valueOf(queueSize);
		}

		builder
			.setAuthor(title, audioTrack.getInfo().uri, user.getEffectiveAvatarUrl())
			.addField("Position in queue", nowPlaying, true)
			.setFooter("Audio track " + currentAudioTrack);

		return builder;
	}

	/**
	 * Creates an embed when adding tracks to the top of the queue using information from the track and user
	 * @param audioTrack The track that was added to the top of the queue
	 * @param user The user to display on an embed
	 * @param playNow If the track was played immediately
	 * @return The embed builder with track information
	 */
	private EmbedBuilder topEmbed(AudioTrack audioTrack, User user, boolean playNow, int currentAudioTrack) {
		String title, position;
		if (playNow) {
			title = "▶️ Playing song now:";
			position = "Now playing";
		} else {
			title = "\uD83D\uDD1D Adding song to top of the queue";
			position = "1";
		}

		return new EmbedBuilder()
			.setTitle(audioTrack.getInfo().title, audioTrack.getInfo().uri)
			.setColor(RandomColor.getRandomColor())
			.setAuthor(title, audioTrack.getInfo().uri, user.getEffectiveAvatarUrl())
			.addField("Channel", audioTrack.getInfo().author, true)
			.addField("Song Duration", DateAndTime.formatMilliseconds(audioTrack.getDuration()), true)
			.addField("Position in queue", position, true)
			.setFooter("Audio track " + currentAudioTrack);
	}

	/**
	 * Returns the volume setting from the music config
	 * @return The volume setting normalized for the bot
	 */
	private int loadVolume(long guildId, int trackNum) {
		String volume = MusicDB.getTrackVolume(guildId, trackNum);
		int calculateVolume;
		try {
			int volumeNum = Integer.parseInt(volume);

			if ((volumeNum < 1) || (volumeNum > 100))
				return 25;

			calculateVolume = volumeNum;
		} catch (NumberFormatException e) {
			calculateVolume = 100;
		}

		return (int) Math.ceil((double) calculateVolume / 4);
	}
}
