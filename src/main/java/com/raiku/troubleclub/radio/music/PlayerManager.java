package com.raiku.troubleclub.radio.music;

import com.raikuman.botutilities.configs.ConfigIO;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
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
 * @version 1.1 2022-23-06
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

	public GuildMusicManager getMusicManager(Guild guild) {
		return this.musicManagerMap.computeIfAbsent(guild.getIdLong(), guildId -> {
			final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager);
			guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
			return guildMusicManager;
		});
	}

	public void loadAndPlay(TextChannel channel, String trackUrl, User user) {
		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

		musicManager.audioPlayer.setVolume(loadVolume());

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				musicManager.trackScheduler.queue(audioTrack);

				channel.sendMessageEmbeds(
					trackEmbed(
						musicManager.trackScheduler.queue.size(),
						audioTrack,
						user
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

					musicManager.trackScheduler.queue(firstTrack);

					channel.sendMessageEmbeds(
						trackEmbed(
							musicManager.trackScheduler.queue.size(),
							firstTrack,
							user
						).build()
					).queue();

					return;
				}

				// Queue playlist if provided a link
				for (AudioTrack track : tracks)
					musicManager.trackScheduler.queue(track);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("Adding playlist to queue:", null, user.getAvatarUrl())
					.setTitle(audioPlaylist.getName(), tracks.get(0).getInfo().uri)
					.setColor(RandomColor.getRandomColor());
				builder
					.addField("Songs in Playlist", "`" + audioPlaylist.getTracks().size() + "`songs", true);

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

	public void loadToTop(TextChannel channel, String trackUrl, User user, boolean playNow) {
		GuildMusicManager musicManager = this.getMusicManager(channel.getGuild());

		musicManager.audioPlayer.setVolume(loadVolume());

		this.audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack audioTrack) {
				musicManager.trackScheduler.addToTop(audioTrack);

				if (playNow)
					musicManager.trackScheduler.nextTrack();

				channel.sendMessageEmbeds(
					topEmbed(
						audioTrack,
						user,
						playNow
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

					musicManager.trackScheduler.addToTop(firstTrack);

					if (playNow)
						musicManager.trackScheduler.nextTrack();

					channel.sendMessageEmbeds(
						topEmbed(
							firstTrack,
							user,
							playNow
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

	public static PlayerManager getInstance() {
		if (PLAYER_INSTANCE == null) {
			PLAYER_INSTANCE = new PlayerManager();
		}

		return PLAYER_INSTANCE;
	}

	private EmbedBuilder trackEmbed(int queueSize, AudioTrack audioTrack, User user) {
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
			.addField("Position in queue", nowPlaying, true);

		return builder;
	}

	private EmbedBuilder topEmbed(AudioTrack audioTrack, User user, boolean playNow) {
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
			.addField("Position in queue", position, true);
	}

	private int loadVolume() {
		String volume = ConfigIO.readConfig("musicSettings", "volume");
		try {
			int volumeNum = Integer.parseInt(volume);

			if ((volumeNum < 1) || (volumeNum > 25))
				return 25;

			return volumeNum;
		} catch (NumberFormatException e) {
			return 25;
		}
	}
}
