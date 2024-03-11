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
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public class Stop extends Command {
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
        AudioTrack currentTrack = musicManager.getCurrentAudioPlayer().getPlayingTrack();
        int numCleared = Clear.clearQueue(ctx);
        musicManager.getCurrentTrackScheduler().nextTrack();

        // Unpause if paused
        if (musicManager.getCurrentAudioPlayer().isPaused()) {
            musicManager.getCurrentAudioPlayer().setPaused(false);
        }

        String title;
        if (currentTrack != null) {
            if (numCleared > 0) {
                title = "\uD83D\uDED1 Stopped current song and removed " + numCleared + " songs from queue!";
            } else {
                title = "\uD83D\uDED1 Stopped current song!";
            }
        } else {
            if (numCleared > 0) {
                title = "\uD83D\uDED1 Removed " + numCleared + " songs from queue!";
            } else {
                title = "\uD83D\uDED1 No song is playing and nothing from the queue to remove!";
            }
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
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stops the current track's playing song and clears the queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
