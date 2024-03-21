package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.List;

public class SkipTo extends Command {

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

        // Handle command
        if (ctx.args().isEmpty()) {
            defaultHandle(ctx);
        } else {
            skipToTarget(ctx);
        }
    }

    @Override
    public String getInvoke() {
        return "skipto";
    }

    @Override
    public List<String> getAliases() {
        return List.of("st");
    }

    @Override
    public String getUsage() {
        return "(<song #>)";
    }

    @Override
    public String getDescription() {
        return "Skips the current track's playing song to a song in the queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    private void defaultHandle(CommandContext ctx) {
        Skip.skipToNextTrack(ctx);
    }

    private void skipToTarget(CommandContext ctx) {
        // Get first arg
        String firstArg = ctx.args().get(0);
        int songNum;
        try {
            songNum = Integer.parseInt(firstArg);
        } catch (NumberFormatException e) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Check if song num is in range of queue
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();
        if (songNum < 0 || songNum > trackScheduler.queue.size()) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    "⏭ Could not skip to your song!",
                    "Your song number `" + songNum + "` is not in range within the queue of `" + trackScheduler.queue.size() + "`!",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        } else {
            // Skip to song
            List<AudioTrack> queueTracks = new ArrayList<>();
            trackScheduler.queue.drainTo(queueTracks);

            AudioTrack currentTrack = null;
            for (int i = songNum - 1; i < queueTracks.size(); i++) {
                if (currentTrack == null) {
                    currentTrack = queueTracks.get(i);
                }

                trackScheduler.queue(queueTracks.get(i));
            }

            trackScheduler.nextTrack();

            if (currentTrack != null) {
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    NowPlaying.songInfoEmbed(ctx, currentTrack, musicManager.getCurrentAudioPlayerNum())
                        .setAuthor("⏭ Playing next song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl()));
            } else {
                // Nothing to skip to
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "⏭ Skipped song to empty queue",
                        "",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor())
                        .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
            }

            ctx.event().getMessage().delete().queue();
        }
    }
}
