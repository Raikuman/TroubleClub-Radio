package com.raikuman.troubleclub.radio.commands.music;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.troubleclub.radio.category.MusicCategory;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Handles adding a new song to the top of the queue
 *
 * @version 1.6 2022-16-07
 * @since 1.1
 */
public class PlayTop implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();
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

		if (!memberVoiceState.inAudioChannel()) {
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

		String link = String.join(" ", ctx.getArgs());
		if (!isUrl(link))
			link = "ytsearch:" + link;

		PlayerManager.getInstance().loadToTop(channel, link, ctx.getEvent().getAuthor(), false, ctx.getGuild().getIdLong());

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "playtop";
	}

	@Override
	public String getUsage() {
		return "<link>";
	}

	@Override
	public String getDescription() {
		return "Plays a song or playlist from a link, or search for a song to play and add it to the " +
			"beginning of the queue";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"playt",
			"top"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new MusicCategory();
	}

	/**
	 * Check if the string input is a url
	 * @param url Url string
	 * @return If string is url
	 */
	private boolean isUrl(String url) {
		try {
			new URI(url);
			return true;
		} catch (URISyntaxException e) {
			return false;
		}
	}
}
