package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

/**
 * Handles the bot leaving the voice channel of a user
 *
 * @version 1.7 2023-11-01
 * @since 1.1
 */
public class Leave implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
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
