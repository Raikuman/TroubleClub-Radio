package com.raikuman.troubleclub.radio.music;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

public class MusicChecking {

    private Guild guild;
    private MessageChannelUnion channel;
    private Message message;
    private Member member, self;
    private boolean isMemberNotInVoiceChannel, isBotInDifferentVoiceChannel, lacksPermissionToJoin, isPrivateMessage, checkBotInVoiceChannel;

    private MusicChecking(Guild guild, MessageChannelUnion channel, Message message, Member member) {
        this.guild = guild;
        this.channel = channel;
        this.message = message;
        this.member = member;
        if (guild != null) {
            this.self = guild.getSelfMember();
        } else {
            this.self = null;
        }

        this.isMemberNotInVoiceChannel = false;
        this.isBotInDifferentVoiceChannel = false;
        this.lacksPermissionToJoin = false;
        this.isPrivateMessage = false;
        this.checkBotInVoiceChannel = true;
    }

    public static MusicChecking setup(Guild guild, MessageChannelUnion channel, Message message, Member member) {
        return new MusicChecking(guild, channel, message, member);
    }

    public MusicChecking checkMemberNotInVoiceChannel(boolean check) {
        this.isMemberNotInVoiceChannel = check;
        return this;
    }

    public MusicChecking checkBotInDifferentVoiceChannel(boolean check, boolean checkBotInVoiceChannel) {
        this.isBotInDifferentVoiceChannel = check;
        this.checkBotInVoiceChannel = checkBotInVoiceChannel;
        return this;
    }

    public MusicChecking checkLacksPermission(boolean check) {
        this.lacksPermissionToJoin = check;
        return this;
    }

    public MusicChecking checkPrivateMessage(boolean check) {
        this.isPrivateMessage = check;
        return this;
    }

    public boolean check() {
        if (isPrivateMessage) {
            if (inPrivateMessage()) {
                return true;
            }
        }

        if (isMemberNotInVoiceChannel) {
            if (isMemberNotInVoiceChannel()) {
                System.out.println("HERE");
                return true;
            }
        }

        if (isBotInDifferentVoiceChannel) {
            if (isBotInDifferentVoiceChannel()) {
                return true;
            }
        }

        if (lacksPermissionToJoin) {
            if (lacksPermissionToJoin()) {
                return true;
            }
        }

        return false;
    }

    private boolean isMemberNotInVoiceChannel() {
        if (member == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the member when checking for a voice channel.",
                    channel, self.getUser()));
            return true;
        }

        GuildVoiceState memberVoiceState = member.getVoiceState();
        if (memberVoiceState == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Something went wrong with Discord!",
                    "An error occurred getting the voice state for the member when checking for a voice channel.",
                    channel, self.getUser()));
            return true;
        }

        if (memberVoiceState.getChannel() != null) {
            return false;
        } else {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("You are not in a voice channel!", "Connect to a voice channel to use this command.",
                    channel, self.getUser()));
            return true;
        }
    }

    private boolean isBotInDifferentVoiceChannel() {
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
            if (checkBotInVoiceChannel) {
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

    private boolean lacksPermissionToJoin() {
        if (self.hasPermission(Permission.VOICE_CONNECT)) {
            return false;
        } else {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("I can't join your voice channel!", "Allow permission to connect to voice channels.",
                    channel, self.getUser()));
            return true;
        }
    }

    private boolean inPrivateMessage() {
        if (guild == null) {
            MessageResources.embedReplyDelete(message, 10, true,
                EmbedResources.error("Cannot use this method in direct messages!",
                    "Use this method in a server.",
                    channel, self.getUser()));
            return true;
        }

        return false;
    }

    public static AudioChannelUnion retrieveMemberVoiceChannel(Member member) {
        if (member == null) return null;

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null) return null;

        return voiceState.getChannel();
    }
}
