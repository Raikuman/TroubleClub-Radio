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
import java.util.Collections;
import java.util.List;

public class Shuffle extends Command {
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

        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();
        if (!trackScheduler.queue.isEmpty()) {
            // Handle shuffling
            List<AudioTrack> queueTracks = new ArrayList<>();
            trackScheduler.queue.drainTo(queueTracks);
            Collections.shuffle(queueTracks);
            for (AudioTrack audioTrack : queueTracks) {
                trackScheduler.queue.offer(audioTrack);
            }

            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    "\uD83D\uDD00 Shuffling " + trackScheduler.queue.size() + " songs!",
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    "\uD83D\uDD00 There's nothing in the queue to shuffle!",
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "shuffle";
    }

    @Override
    public List<String> getAliases() {
        return List.of("sh");
    }

    @Override
    public String getDescription() {
        return "Shuffles the current track's queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
