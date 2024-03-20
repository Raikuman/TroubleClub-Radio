package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.List;

public class Leave extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (MusicChecking.setup(
                ctx.event().getGuild(),
                ctx.event().getChannel(),
                ctx.event().getMessage(),
                ctx.event().getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, true)
            .check()) {
            return;
        }

        GuildVoiceState selfVoiceState = ctx.event().getGuild().getSelfMember().getVoiceState();
        if (selfVoiceState == null) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member and bot user when checking for different voice channels",
                    ctx.event().getChannel(), ctx.event().getGuild().getSelfMember().getUser()));
            return;
        }

        AudioChannelUnion botChannel = selfVoiceState.getChannel();
        if (botChannel == null) {
            // Send not in voice channel embed
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error("I'm not in a voice channel!",
                    "I must be in a voice channel to use this command!",
                    ctx.event().getChannel(), ctx.event().getGuild().getSelfMember().getUser()));
        } else {
            ctx.event().getGuild().getAudioManager().closeAudioConnection();

            // Leave reaction
            ctx.event().getMessage().addReaction(Emoji.fromFormatted("\uD83D\uDC4B")).queue();
        }
    }

    @Override
    public String getInvoke() {
        return "leave";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l");
    }

    @Override
    public String getDescription() {
        return "Leaves a user's voice channel.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
