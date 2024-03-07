package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.pagination.Pagination;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Queue extends Command {

    @Override
    public void handle(CommandContext ctx) {
        // Check if member is in a voice channel
        if (MusicChecking.isMemberNotInVoiceChannel(ctx)) {
            return;
        }

        // Check if bot is in a voice channel that is not the member's
        if (MusicChecking.isBotInDifferentVoiceChannel(ctx, true)) {
            return;
        }

        // Build strings
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();

        List<String> queueStrings = new ArrayList<>();
        String playerStatus, currentTrackStatus = "Playing:";
        // Handle number of songs
        int totalSongs = trackScheduler.queue.size() + 1;
        playerStatus = "*" + totalSongs + " song";
        if (totalSongs > 1) {
            playerStatus += "s";
        }
        playerStatus += "*";

        if (trackScheduler.isRepeat()) {
            playerStatus += "\n\uD83D\uDD04 Repeating song";
            currentTrackStatus = "Repeating:";
        }

        if (trackScheduler.isRepeatQueue()) {
            playerStatus += "\n\uD83D\uDD01 Looping queue";
        }

        if (musicManager.getCurrentAudioPlayer().isPaused()) {
            currentTrackStatus = "Paused:";
        }

        // Handle nothing
        AudioTrack currentTrack = trackScheduler.audioPlayer.getPlayingTrack();
        if (currentTrack == null) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "Nothing in the queue!",
                        playerStatus,
                        ctx.event().getChannel(),
                        ctx.event().getAuthor()));

            ctx.event().getMessage().delete().queue();
            return;
        }

        // Add player status
        queueStrings.add(playerStatus);

        StringBuilder queueBuilder = new StringBuilder();

        // Handle playing track
        queueBuilder
            .append("`")
            .append(currentTrackStatus)
            .append("` ")
            .append(getSongString(currentTrack));

        queueStrings.add(queueBuilder.toString());

        // Build queue strings
        int songNum = 1;
        for (AudioTrack audioTrack : trackScheduler.queue) {
            queueBuilder = new StringBuilder();

            queueBuilder
                .append("`")
                .append(songNum)
                .append(".` ")
                .append(getSongString(audioTrack));

            queueStrings.add(queueBuilder.toString());
            songNum++;
        }

        // Build pages
        queueBuilder = new StringBuilder();
        int currentCharacterCount = 0;
        List<EmbedBuilder> pages = new ArrayList<>();
        for (int i = 0; i < queueStrings.size(); i++) {
            if (i != 0) {
                queueBuilder.append("\n\n");
            }

            queueBuilder.append(queueStrings.get(i));

            currentCharacterCount += queueStrings.get(i).length();
            if (currentCharacterCount >= 550 || i == queueStrings.size() - 1) {
                currentCharacterCount = 0;

                pages.add(EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    "",
                    queueBuilder.toString(),
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));

                queueBuilder = new StringBuilder();
            }
        }

        // Build pages
        new Pagination(
            ctx.event().getAuthor(),
            "Track " + musicManager.getCurrentAudioPlayerNum() + " Queue",
            (messageChannelUnion, user) -> pages,
            componentHandler)
            .setLooping(true)
            .setHasFirstPage(true)
            .sendPagination(ctx);
    }

    @Override
    public String getInvoke() {
        return "queue";
    }

    @Override
    public List<String> getAliases() {
        return List.of("q");
    }

    @Override
    public String getDescription() {
        return "Shows the queue of songs on the current track.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    private String getSongString(AudioTrack audioTrack) {
        AudioTrackInfo audioTrackInfo = audioTrack.getInfo();

        Duration trackDuration = Duration.ofMillis(audioTrackInfo.length);
        String trackLength = String.format("%02d:%02d:%02d",
            trackDuration.toHours(),
            trackDuration.toMinutesPart(),
            trackDuration.toSecondsPart());

        return String.format(
            "[%s](%s) | `%s`", audioTrackInfo.title, audioTrackInfo.uri, trackLength);
    }
}
