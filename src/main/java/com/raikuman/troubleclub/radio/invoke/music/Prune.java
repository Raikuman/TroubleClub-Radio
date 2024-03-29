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
import java.util.stream.Collectors;

public class Prune extends Command {

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

        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        int numPruned = pruneSongs(musicManager.getCurrentTrackScheduler());

        String title;
        if (numPruned > 0) {
            title = "✂️ Pruned " + numPruned + " song";
            if (numPruned > 1) {
                title += "s";
            }

            title += " from the queue!";
        } else {
            title = "✂️ No songs were pruned from the queue!";
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    title,
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "prune";
    }

    @Override
    public String getDescription() {
        return "Prunes the current track's queue of duplicate songs.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    public static int pruneSongs(TrackScheduler trackScheduler) {
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);

        List<String> identifiers = queueTracks.stream().map(AudioTrack::getIdentifier).collect(Collectors.toList());
        int numPruned = 0;
        for (AudioTrack audioTrack : queueTracks) {
            if (identifiers.contains(audioTrack.getIdentifier())) {
                trackScheduler.queue(audioTrack);
                identifiers.remove(audioTrack.getIdentifier());
                numPruned++;
            }
        }

        return numPruned;
    }
}
