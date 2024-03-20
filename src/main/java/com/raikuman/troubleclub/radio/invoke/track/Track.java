package com.raikuman.troubleclub.radio.invoke.track;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.time.Duration;
import java.util.List;

public class Track extends Command {

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

        if (ctx.args().isEmpty()) {
            GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());

            // Send track embed
            MessageResources.embedDelete(ctx.event().getChannel(), 30,
                EmbedResources.defaultResponse(
                    MusicManager.TRACK_COLOR,
                    "\uD83C\uDFBC Current track is track " + musicManager.getCurrentAudioPlayerNum(),
                    trackString(musicManager.getCurrentTrackScheduler()),
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()));

            ctx.event().getMessage().delete().queue();
        } else if (ctx.args().size() == 1) {
            if (!updateTrack(ctx)) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            } else {
                ctx.event().getMessage().delete().queue();
            }
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
        }
    }

    @Override
    public String getInvoke() {
        return "track";
    }

    @Override
    public List<String> getAliases() {
        return List.of("t");
    }

    @Override
    public String getUsage() {
        return "(<track 1-" + GuildMusicManager.MAX_AUDIO_PLAYERS + ">)";
    }

    @Override
    public String getDescription() {
        return "Check the status of the current track, or change the current track to a different track.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new com.raikuman.troubleclub.radio.invoke.category.Track());
    }

    public static String trackString(TrackScheduler trackScheduler) {
        StringBuilder trackBuilder = new StringBuilder();

        // Is playing
        if (trackScheduler.audioPlayer.getPlayingTrack() != null) {
            if (trackScheduler.audioPlayer.isPaused()) {
                trackBuilder.append("⏸ *Paused*");
            } else {
                trackBuilder.append("▶️ *Playing*");
            }
            trackBuilder.append("\n");

            int totalSongs = trackScheduler.queue.size();
            trackBuilder
                .append("*")
                .append(totalSongs)
                .append(" song");
            if (totalSongs > 1) {
                trackBuilder.append("s");
            }
            trackBuilder.append("*\n");

            // Queue length
            long queueLength = trackScheduler.audioPlayer.getPlayingTrack().getPosition();
            for (AudioTrack audioTrack : trackScheduler.queue) {
                queueLength += audioTrack.getDuration();
            }

            Duration queueDuration = Duration.ofMillis(queueLength);
            String lengthString = String.format("%02d:%02d:%02d",
                queueDuration.toHours(),
                queueDuration.toMinutesPart(),
                queueDuration.toSecondsPart());

            trackBuilder
                .append("Length: `")
                .append(lengthString)
                .append("`");
        } else {
            trackBuilder.append("⏹ Nothing playing");
        }

        // Repeating song
        if (trackScheduler.isRepeat()) {
            if (!trackBuilder.isEmpty()) {
                trackBuilder.append("\n");
            }

            trackBuilder.append("\uD83D\uDD04 Repeating song");
        }

        // Looping queue
        if (trackScheduler.isRepeatQueue()) {
            if (!trackBuilder.isEmpty()) {
                trackBuilder.append("\n");
            }

            trackBuilder.append("\uD83D\uDD01 Looping queue");
        }

        return trackBuilder.toString();
    }

    private boolean updateTrack(CommandContext ctx) {
        boolean updated = false;

        int trackNum;
        try {
            trackNum = Integer.parseInt(ctx.args().get(0));
        } catch (NumberFormatException e) {
            trackNum = 0;
        }

        if (trackNum >= 1 && trackNum <= GuildMusicManager.MAX_AUDIO_PLAYERS) {
            MusicManager.getInstance().getMusicManager(ctx.event().getGuild()).setCurrentAudioPlayer(trackNum);

            // Send track embed
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.TRACK_COLOR,
                    "\uD83C\uDFBC Set current track to track " + trackNum,
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor()
                ));

            updated = true;
        }

        return updated;
    }
}
