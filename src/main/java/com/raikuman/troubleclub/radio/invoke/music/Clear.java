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

import java.util.List;

public class Clear extends Command {

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
        int numCleared = clearQueue(musicManager.getCurrentTrackScheduler());

        if (numCleared > 0) {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                    MusicManager.MUSIC_COLOR,
                    "\uD83D\uDDD1️ Removed " + numCleared + " songs from the queue!",
                    "",
                    ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "\uD83D\uDDD1️ Nothing from the queue to remove!",
                        "",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        }


        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "clear";
    }

    @Override
    public List<String> getAliases() {
        return List.of("c");
    }

    @Override
    public String getDescription() {
        return "Clears the current track's queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    public static int clearQueue(TrackScheduler trackScheduler) {

        int numSongs = trackScheduler.queue.size();
        trackScheduler.queue.clear();

        return numSongs;
    }
}
