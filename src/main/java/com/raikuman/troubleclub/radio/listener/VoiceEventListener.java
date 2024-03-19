package com.raikuman.troubleclub.radio.listener;

import com.raikuman.troubleclub.radio.invoke.music.Clear;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class VoiceEventListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(VoiceEventListener.class);

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("{}" + VoiceEventListener.class.getName() + " is initialized",
            event.getJDA().getSelfUser().getEffectiveName());
    }

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        // Check only leaving events
        AudioChannelUnion channel = event.getChannelLeft();
        if (channel == null) {
            return;
        }

        // Check if bot is in the same channel
        Guild guild = event.getGuild();
        if (!isBotInVoiceChannel(guild, channel)) {
            return;
        }

        // Check the number of real users in the channel
        if (realMembersAmount(channel) != 0) {
            return;
        }

        // Stop, clear, and reset all tracks
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(event.getGuild());
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            TrackScheduler trackScheduler = musicManager.getTrackScheduler(i);

            Clear.clearQueue(trackScheduler);
            if (trackScheduler.audioPlayer.getPlayingTrack() != null) {
                trackScheduler.nextTrack();
            }

            if (trackScheduler.isRepeatQueue()) {
                trackScheduler.setRepeatQueue(false);
            }

            if (trackScheduler.isRepeat()) {
                trackScheduler.setRepeat(false);
            }

            if (trackScheduler.audioPlayer.isPaused()) {
                trackScheduler.audioPlayer.setPaused(false);
            }
         }

        // Handle disconnecting from the voice channel
        event.getGuild().getAudioManager().closeAudioConnection();
    }

    private boolean isBotInVoiceChannel(Guild guild, AudioChannelUnion channel) {
        GuildVoiceState selfVoiceState = guild.getSelfMember().getVoiceState();
        if (selfVoiceState == null) {
            return false;
        }

        AudioChannelUnion selfChannel = selfVoiceState.getChannel();
        if (selfChannel == null) {
            return false;
        }

        return selfChannel.equals(channel);
    }

    private int realMembersAmount(AudioChannelUnion channel) {
        List<Member> members = channel.getMembers();
        int numReal = 0;
        for (Member member : members) {
            if (!member.getUser().isBot()) {
                numReal++;
            }
        }

        return numReal;
    }
}
