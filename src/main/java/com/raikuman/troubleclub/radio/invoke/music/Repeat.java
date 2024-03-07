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
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class Repeat extends Command {

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
        trackScheduler.setRepeat(!trackScheduler.isRepeat());

        // Check if there is anything playing
        AudioTrack currentTrack = musicManager.getCurrentAudioPlayer().getPlayingTrack();

        EmbedBuilder embedBuilder;
        String title;
        if (trackScheduler.isRepeat()) {
            if (currentTrack == null) {
                title = "\uD83D\uDD04 Will repeat the next playing song!";
            } else {
                title = "\uD83D\uDD04 Repeating song:";
            }
        } else {
            if (currentTrack == null) {
                title = "\uD83D\uDD04 Stopped repeating!";
            } else {
                title = "\uD83D\uDD04 Stopped repeating song:";
            }
        }

        if (currentTrack != null) {
            embedBuilder = NowPlaying.songInfoEmbed(ctx, currentTrack, musicManager.getCurrentAudioPlayerNum())
                .setAuthor(title, null, ctx.event().getAuthor().getEffectiveAvatarUrl());
        } else {
            embedBuilder = EmbedResources.defaultResponse(
                MusicManager.MUSIC_COLOR,
                    title,
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
                .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName());
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            embedBuilder);
        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "repeat";
    }

    @Override
    public List<String> getAliases() {
        return List.of("r");
    }

    @Override
    public String getDescription() {
        return "Repeats the current track's playing song";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
