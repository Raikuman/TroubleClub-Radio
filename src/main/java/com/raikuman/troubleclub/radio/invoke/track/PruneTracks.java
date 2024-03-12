package com.raikuman.troubleclub.radio.invoke.track;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Track;
import com.raikuman.troubleclub.radio.invoke.music.Prune;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class PruneTracks extends Command {

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
        StringBuilder pruneBuilder = new StringBuilder();
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            if (i != 1) {
                pruneBuilder.append("\n\n");
            }

            pruneBuilder
                .append("**Track ")
                .append(i)
                .append("**\n");

            int numPruned = Prune.pruneSongs(musicManager.getTrackScheduler(i));
            if (numPruned > 0) {
                pruneBuilder
                    .append("✂️ *Pruned `")
                    .append(numPruned)
                    .append(" song");

                if (numPruned != 1) {
                    pruneBuilder.append("s");
                }

                pruneBuilder
                    .append("` from queue.*");

            } else {
                pruneBuilder.append("*Nothing pruned.");
            }
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                "\uD83D\uDED1 Pruning Tracks",
                pruneBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()));

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "prunetracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("prunet");
    }

    @Override
    public String getDescription() {
        return "Prunes all tracks' queues of duplicate songs.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }
}
