package com.raikuman.troubleclub.radio.invoke.track;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Track;
import com.raikuman.troubleclub.radio.invoke.music.Clear;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class ClearTracks extends Command {

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
        StringBuilder clearBuilder = new StringBuilder();
        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            if (i != 1) {
                clearBuilder.append("\n\n");
            }

            int numCleared = Clear.clearQueue(musicManager.getTrackScheduler(i));

            clearBuilder
                .append("**Track ")
                .append(i)
                .append("**: \uD83D\uDED1 *Cleared `")
                .append(numCleared)
                .append(" song");

            if (numCleared != 1) {
                clearBuilder.append("s");
            }

            clearBuilder
                .append("` from queue.*");
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                "\uD83D\uDDD1️ Clearing Tracks",
                clearBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()));

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "cleartracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ct", "cleart");
    }

    @Override
    public String getDescription() {
        return "Clears the queue of all tracks.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }
}
