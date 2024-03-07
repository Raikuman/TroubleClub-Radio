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

public class Loop extends Command {

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
        trackScheduler.setRepeatQueue(!trackScheduler.isRepeatQueue());

        String title;
        if (trackScheduler.isRepeatQueue()) {
            title = "\uD83D\uDD01 Now repeating track " + musicManager.getCurrentAudioPlayerNum() + "'s queue!";
        } else {
            title = "\uD83D\uDD01 Stopped repeating track " + musicManager.getCurrentAudioPlayerNum() + "'s queue!";
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
        return "loop";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l");
    }

    @Override
    public String getDescription() {
        return "Loops the current track's queue of songs.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
