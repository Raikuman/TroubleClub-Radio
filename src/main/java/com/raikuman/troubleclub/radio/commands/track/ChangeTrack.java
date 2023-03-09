package com.raikuman.troubleclub.radio.commands.track;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

/**
 * Handles switching audio tracks of the guild music manager
 *
 * @version 1.4 2023-11-01
 * @since 1.1
 */
public class ChangeTrack implements CommandInterface {

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

		if (ctx.getArgs().size() > 1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		if (ctx.getArgs().size() == 0) {
			EmbedBuilder builder = new EmbedBuilder()
				.setAuthor("\uD83C\uDFB6️Currently on audio track " + musicManager.getCurrentAudioTrack(),
					null,
					ctx.getEventMember().getEffectiveAvatarUrl()
				)
				.setColor(RandomColor.getRandomColor());

			ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
		} else if (ctx.getArgs().size() == 1) {
			try {
				int trackNum = Integer.parseInt(ctx.getArgs().get(0));

				if ((trackNum < 1) || (trackNum > 3)) {
					MessageResources.timedMessage(
						"You must switch to a valid audio track number (1-3)",
						channel,
						5
					);
					return;
				}

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor("\uD83C\uDFB6️Switched to audio track " + trackNum,
						null,
						ctx.getEventMember().getEffectiveAvatarUrl()
					)
					.setColor(RandomColor.getRandomColor());

				musicManager.setCurrentAudioTrack(trackNum);

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
			}
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "changetrack";
	}

	@Override
	public String getUsage() {
		return "(<1-3>)";
	}

	@Override
	public String getDescription() {
		return "Changes the audio track of the current music manager or show which audio track you " +
			"are currently on";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"track",
			"t"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new TrackCategory();
	}
}
