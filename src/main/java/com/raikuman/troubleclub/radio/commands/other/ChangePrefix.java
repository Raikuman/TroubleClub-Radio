package com.raikuman.troubleclub.radio.commands.other;

import com.raikuman.botutilities.commands.manager.CategoryInterface;
import com.raikuman.botutilities.commands.manager.CommandContext;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.configs.Prefix;
import com.raikuman.botutilities.helpers.MessageResources;
import com.raikuman.botutilities.helpers.RandomColor;
import com.raikuman.troubleclub.radio.category.OtherCategory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Handles setting the prefix of the bot
 *
 * @version 1.1 2022-15-07
 * @since 1.2
 */
public class ChangePrefix implements CommandInterface {

	@Override
	public void handle(CommandContext ctx) {
		final TextChannel channel = ctx.getChannel();

		if (!ctx.getEventMember().hasPermission(Permission.ADMINISTRATOR)) {
			MessageResources.timedMessage(
				"You must be an administrator to use this command",
				channel,
				5
			);
			return;
		}

		if (ctx.getArgs().isEmpty() || ctx.getArgs().size() > 1) {
			MessageResources.timedMessage(
				"You must provide a valid argument for this command: `" + getUsage() + "`",
				channel,
				5
			);
			return;
		}

		Prefix.updatePrefix(
			ctx.getGuild().getIdLong(),
			ctx.getArgs().get(0)
		);

		EmbedBuilder builder = new EmbedBuilder()
			.setAuthor(
				"⚙️Set prefix to `" + Prefix.getPrefix(ctx.getGuild().getIdLong()) + "`",
				null,
				ctx.getEventMember().getEffectiveAvatarUrl()
			)
			.setColor(RandomColor.getRandomColor());

		ctx.getChannel().sendMessageEmbeds(builder.build()).queue();

		ctx.getEvent().getMessage().delete().queue();
	}

	@Override
	public String getInvoke() {
		return "changeprefix";
	}

	@Override
	public String getUsage() {
		return "<prefix>";
	}

	@Override
	public String getDescription() {
		return "Changes the current prefix of the bot";
	}

	@Override
	public CategoryInterface getCategory() {
		return new OtherCategory();
	}
}
