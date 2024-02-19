package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.components.components.PaginationComponent;
import com.raikuman.botutilities.invokes.components.manager.ComponentHandler;
import com.raikuman.botutilities.invokes.components.manager.ComponentInvoke;
import com.raikuman.botutilities.invokes.components.pagination.PaginationBuilder;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.context.EventContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.helpers.DateAndTime;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles sending a pagination of songs queued and playing, as well as the state of the audio player
 *
 * @version 1.9 2023-25-06
 * @since 1.1
 */
public class Queue extends ComponentInvoke implements CommandInterface {

	public Queue() {
		componentHandler = ComponentHandler.pagination(new PaginationComponent(
			new PaginationBuilder(getInvoke())
				.setTitle("Music Queue")
				.setItemsPerPage(10)
				.enableLoop(true)
				.enableFirstPageButton(true)
				.build()
		));
	}

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel().asTextChannel();
		final Member self = ctx.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		if (selfVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		GuildVoiceState memberVoiceState = ctx.getEventMember().getVoiceState();
		if (memberVoiceState == null) {
			MessageResources.connectError(channel, 5);
			return;
		}

		if (!memberVoiceState.inAudioChannel() || (memberVoiceState.getGuild() != ctx.getGuild())) {
			MessageResources.timedMessage(
				"You must be in a voice channel to use this command",
				channel,
				5
			);
			return;
		}

		if (selfVoiceState.inAudioChannel() && (selfVoiceState.getChannel() != memberVoiceState.getChannel())) {
			if (selfVoiceState.getChannel() == null) {
				MessageResources.connectError(channel, 5);
				return;
			}

			MessageResources.timedMessage(
				"You must be in `" + selfVoiceState.getChannel().getName() + "` to use this command",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();

		if ((audioPlayer.getPlayingTrack() == null) && trackScheduler.queue.isEmpty()) {
			MessageResources.timedMessage(
				"The queue is currently empty",
				channel,
				5
			);
			return;
		}

		componentHandler.providePaginationComponent().updateItems(ctx.getEventMember(), pageStrings(ctx));
		componentHandler.providePaginationComponent().handleContext(ctx);
	}

	@Override
	public String getInvoke() {
		return "queue";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Show the current queue of songs";
	}

	@Override
	public List<String> getAliases() {
		return List.of("q");
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}

	private List<String> pageStrings(EventContext ctx) {
		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final AudioPlayer audioPlayer = musicManager.getAudioPlayer();
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();
		List<String> stringList = new ArrayList<>();

		StringBuilder playerState = new StringBuilder();
		if (trackScheduler.repeat)
			playerState.append("\uD83D\uDD01 Repeating current track");
		else if (trackScheduler.repeatQueue)
			playerState.append("\uD83D\uDD03 Repeating queue");

		playerState.append("\n");

		if (audioPlayer.isPaused())
			playerState.append("`Paused:` ");
		else
			playerState.append("`Playing:` ");

		AudioTrackInfo currentTrackInfo = audioPlayer.getPlayingTrack().getInfo();
		playerState.append(String.format(
			"[%s](%s) | `%s`",
			currentTrackInfo.title,
			currentTrackInfo.uri,
			DateAndTime.formatMilliseconds(currentTrackInfo.length)
		));
		stringList.add(playerState.toString());

		int songNum = 1;
		for (AudioTrack track : trackScheduler.queue) {
			stringList.add(String.format(
				"`%d.` [%s](%s) | `%s`",
				songNum,
				track.getInfo().title,
				track.getInfo().uri,
				DateAndTime.formatMilliseconds(track.getInfo().length)
			));

			songNum++;
		}

		return stringList;
	}
}
