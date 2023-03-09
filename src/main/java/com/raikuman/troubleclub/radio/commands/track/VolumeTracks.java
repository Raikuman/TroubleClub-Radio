package com.raikuman.troubleclub.radio.commands.track;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.TrackCategory;
import com.raikuman.troubleclub.radio.config.music.MusicDB;
import com.raikuman.troubleclub.radio.music.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.PlayerManager;
import com.raikuman.troubleclub.radio.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Map;

/**
 * Handles setting the volume of all audio tracks of the bot
 *
 * @version 1.5 2023-11-01
 * @since 1.1
 */
public class VolumeTracks implements CommandInterface {

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

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(ctx.getGuild());

		if (ctx.getArgs().size() == 0) {

			StringBuilder stringBuilder = new StringBuilder();

			String volume;
			for (int i = 1; i < 4; i++) {
				volume = MusicDB.getTrackVolume(ctx.getGuild().getIdLong(), i);

				int volumeNum;
				try {
					volumeNum = Integer.parseInt(volume);
				} catch (NumberFormatException e) {
					volumeNum = 100;
				}

				stringBuilder
					.append("**Audio Track ")
					.append(i)
					.append("**: `")
					.append(volumeNum)
					.append("%`");

				if (i != 3)
					stringBuilder.append("\n");
			}

			EmbedBuilder builder = new EmbedBuilder()
				.setAuthor(
					"\uD83D\uDD0A Current volume of audio tracks:",
					null,
					ctx.getEventMember().getEffectiveAvatarUrl()
				)
				.setColor(RandomColor.getRandomColor())
				.setDescription(stringBuilder);

			ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
		}

		if (ctx.getArgs().size() > 2) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().size() == 1) {
			try {
				int volumeNum = Integer.parseInt(ctx.getArgs().get(0));

				if ((volumeNum < 1) || (volumeNum > 100)) {
					MessageResources.timedMessage(
						"You must enter a volume setting between 1-100",
						channel,
						5
					);
					return;
				}

				int calculateVolume = (int) Math.ceil((double) volumeNum / 4);

				for (int i = 1; i < 4; i++) {
					MusicDB.updateTrackVolume(
						ctx.getGuild().getIdLong(),
						i,
						volumeNum
					);
				}

				for (Map.Entry<AudioPlayer, TrackScheduler> entry : musicManager.getPlayerMap().entrySet())
					entry.getValue().audioPlayer.setVolume(calculateVolume);

				EmbedBuilder builder = new EmbedBuilder()
					.setAuthor(
						"\uD83D\uDD0A Set volume of all audio tracks to " + volumeNum + "%",
						null,
						ctx.getEventMember().getEffectiveAvatarUrl()
					)
					.setColor(RandomColor.getRandomColor());

				ctx.getChannel().sendMessageEmbeds(builder.build()).queue();
			} catch (NumberFormatException e) {
				MessageResources.timedMessage(
					"You must provide a valid argument for this command: `" + getUsage() + "`",
					channel,
					5
				);
				return;
			}
		}

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "volumetracks";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getDescription() {
		return "Shows the current volume of all audio tracks of the bot or set the volume of all audio " +
			"tracks of the bot";
	}

	@Override
	public List<String> getAliases() {
		return List.of(
			"volumet",
			"vtracks",
			"vt",
			"voltracks",
			"volt",
			"volumeall",
			"vall"
		);
	}

	@Override
	public CategoryInterface getCategory() {
		return new TrackCategory();
	}
}
