package com.raikuman.troubleclub.radio.music;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public class MusicChecking {

    public static boolean isMemberNotInVoiceChannel(CommandContext ctx) {
        MessageChannelUnion messageChannel = ctx.event().getChannel();
        User user = ctx.event().getAuthor();
        Message message = ctx.event().getMessage();
        Member member = ctx.event().getMember();

        if (member == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the member when checking for a voice channel",
                    messageChannel, ctx.event().getGuild().getSelfMember().getUser()));
            return true;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member when checking for a voice channel",
                    messageChannel, ctx.event().getGuild().getSelfMember().getUser()));
            return true;
        }

        if (memberVoiceState.getChannel() != null) {
            return false;
        } else {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("You are not in a voice channel!", "Connect to a voice channel to use this command",
                    messageChannel, ctx.event().getGuild().getSelfMember().getUser()));
            return true;
        }
    }

    public static boolean isBotInDifferentVoiceChannel(CommandContext ctx) {
        MessageChannelUnion messageChannel = ctx.event().getChannel();
        User user = ctx.event().getAuthor();
        Message message = ctx.event().getMessage();
        Member self = ctx.event().getGuild().getSelfMember();
        Member member = ctx.event().getMember();

        if (member == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the member and bot user when checking for different voice channels",
                    messageChannel, self.getUser()));
            return true;
        }

        // Get voice states
        GuildVoiceState selfVoiceState = self.getVoiceState();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (selfVoiceState == null || memberVoiceState == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member and bot user when checking for different voice channels",
                    messageChannel, self.getUser()));
            return true;
        }

        AudioChannelUnion botChannel = selfVoiceState.getChannel();

        // Check if bot is not in a voice channel
        if (botChannel == null) {
            return false;
        }

        // Check if bot is in a different voice channel
        if (!botChannel.equals(memberVoiceState.getChannel())) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("I'm currently in a different channel!", "Unable to join your voice channel until " +
                        "disconnected from `" + botChannel.getName() + "`",
                    messageChannel, self.getUser()));
            return true;
        }

        return false;
    }

    public static boolean lacksPermissionToJoin(CommandContext ctx) {
        if (ctx.event().getGuild().getSelfMember().hasPermission(Permission.VOICE_CONNECT)) {
            return false;
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error("I can't join your voice channel!", "Allow permission to connect to voice channels",
                    ctx.event().getChannel(), ctx.event().getGuild().getSelfMember().getUser()));
            return true;
        }
    }

    public static AudioChannelUnion retrieveMemberVoiceChannel(CommandContext ctx) {
        Member member = ctx.event().getMember();
        if (member == null) return null;

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null) return null;

        return voiceState.getChannel();
    }
}
