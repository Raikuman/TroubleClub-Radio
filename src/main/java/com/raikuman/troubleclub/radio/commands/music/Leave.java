package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.invokes.CategoryInterface;
import com.raikuman.botutilities.invokes.context.CommandContext;
import com.raikuman.botutilities.invokes.interfaces.CommandInterface;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

/**
 * Handles the bot leaving the voice channel of a user
 *
 * @version 1.9 2023-22-06
 * @since 1.1
 */
public class Leave implements CommandInterface {

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

		boolean stopped = new Stop().stopMusic(ctx);

		if (stopped) {
			ctx.getGuild().getAudioManager().closeAudioConnection();
			ctx.getEvent().getMessage().addReaction(Emoji.fromUnicode("U+1F44B")).queue();
		} else {
			ctx.getEvent().getMessage().addReaction(Emoji.fromUnicode("U+1F6AB")).queue();
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());
		final TrackScheduler trackScheduler = musicManager.getTrackScheduler();
		trackScheduler.queue.clear();
		trackScheduler.audioPlayer.stopTrack();
		trackScheduler.repeat = false;
		trackScheduler.repeatQueue = false;

		musicManager.getAudioPlayer().setPaused(false);
	}

	@Override
	public String getInvoke() {
		return "leave";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Leaves the user's voice channel";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"fuckoff",
			"exit"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}
}
