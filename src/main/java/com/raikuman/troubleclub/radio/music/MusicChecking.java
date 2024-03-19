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
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class MusicChecking {

    public static boolean isMemberNotInVoiceChannel(StringSelectInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            return true;
        }

        return isMemberNotInVoiceChannel(
            ctx.getChannel(),
            ctx.getMessage(),
            ctx.getMember(),
            ctx.getGuild().getSelfMember().getUser()
        );
    }

    public static boolean isMemberNotInVoiceChannel(CommandContext ctx) {
        return isMemberNotInVoiceChannel(
            ctx.event().getChannel(),
            ctx.event().getMessage(),
            ctx.event().getMember(),
            ctx.event().getGuild().getSelfMember().getUser()
        );
    }

    private static boolean isMemberNotInVoiceChannel(MessageChannelUnion channel, Message message, Member member, User self) {
        if (member == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the member when checking for a voice channel.",
                    channel, self));
            return true;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member when checking for a voice channel.",
                    channel, self));
            return true;
        }

        if (memberVoiceState.getChannel() != null) {
            return false;
        } else {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("You are not in a voice channel!", "Connect to a voice channel to use this command.",
                    channel, self));
            return true;
        }
    }

    public static boolean isBotInDifferentVoiceChannel(StringSelectInteractionEvent ctx, boolean checkIsInChannel) {
        if (ctx.getGuild() == null) {
            return true;
        }

        return isBotInDifferentVoiceChannel(
            ctx.getChannel(),
            ctx.getMessage(),
            ctx.getMember(),
            ctx.getGuild().getSelfMember(),
            checkIsInChannel
        );
    }

    public static boolean isBotInDifferentVoiceChannel(CommandContext ctx, boolean checkIsInChannel) {
        return isBotInDifferentVoiceChannel(
            ctx.event().getChannel(),
            ctx.event().getMessage(),
            ctx.event().getMember(),
            ctx.event().getGuild().getSelfMember(),
            checkIsInChannel
        );
    }

    private static boolean isBotInDifferentVoiceChannel(MessageChannelUnion channel, Message message, Member member,
                                                       Member self, boolean checkIsInChannel) {
        if (member == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the member and bot user when checking for different voice channels.",
                    channel, self.getUser()));
            return true;
        }

        // Get voice states
        GuildVoiceState selfVoiceState = self.getVoiceState();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (selfVoiceState == null || memberVoiceState == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member and bot user when checking for different voice channels.",
                    channel, self.getUser()));
            return true;
        }

        AudioChannelUnion botChannel = selfVoiceState.getChannel();

        // Check if bot is not in a voice channel
        if (botChannel == null) {
            if (checkIsInChannel) {
                MessageResources.embedReplyDelete(message, 10, true,
                    EmbedResources.error("I must be in a voice channel!",
                        "I need to be connected to a voice channel to use this command.",
                        channel, self.getUser()));
                return true;
            } else {
                return false;
            }
        }

        // Check if bot is in a different voice channel
        if (!botChannel.equals(memberVoiceState.getChannel())) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("I'm currently in a different channel!", "Unable to join your voice channel until " +
                        "disconnected from `" + botChannel.getName() + "`.",
                    channel, self.getUser()));
            return true;
        }

        return false;
    }

    public static boolean lacksPermissionToJoin(StringSelectInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            return true;
        }

        return lacksPermissionToJoin(
            ctx.getChannel(),
            ctx.getMessage(),
            ctx.getGuild().getSelfMember()
        );
    }

    public static boolean lacksPermissionToJoin(CommandContext ctx) {
        return lacksPermissionToJoin(
            ctx.event().getChannel(),
            ctx.event().getMessage(),
            ctx.event().getGuild().getSelfMember()
        );
    }

    private static boolean lacksPermissionToJoin(MessageChannelUnion channel, Message message, Member self) {
        if (self.hasPermission(Permission.VOICE_CONNECT)) {
            return false;
        } else {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("I can't join your voice channel!", "Allow permission to connect to voice channels.",
                    channel, self.getUser()));
            return true;
        }
    }

    public static AudioChannelUnion retrieveMemberVoiceChannel(StringSelectInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            return null;
        }

        return retrieveMemberVoiceChannel(ctx.getMember());
    }

    public static boolean inPrivateMessage(StringSelectInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            MessageResources.embedReplyDelete(ctx.getMessage(), 10, true,
                EmbedResources.error("Cannot use this method in direct messages!",
                    "Use this method in a server.",
                    ctx.getChannel(), ctx.getUser()));
            return true;
        }

        return false;
    }

    public static AudioChannelUnion retrieveMemberVoiceChannel(CommandContext ctx) {
        return retrieveMemberVoiceChannel(ctx.event().getMember());
    }

    private static AudioChannelUnion retrieveMemberVoiceChannel(Member member) {
        if (member == null) return null;

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null) return null;

        return voiceState.getChannel();
    }
}
