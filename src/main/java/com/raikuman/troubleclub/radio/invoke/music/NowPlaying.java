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
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Duration;
import java.util.List;

public class NowPlaying extends Command {

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
        AudioPlayer audioPlayer = musicManager.getCurrentAudioPlayer();
        AudioTrack currentTrack = audioPlayer.getPlayingTrack();

        if (currentTrack != null) {
            // Send embed of current track
            String title = "";
            if (!audioPlayer.isPaused()) {
                title = "▶️ Now playing";
            } else {
                title = "⏸ Paused";
            }

            if (musicManager.getCurrentTrackScheduler().isRepeat()) {
                title += " and repeating";
            }

            title += ":";

            ctx.event().getChannel().sendMessageEmbeds(
                songInfoEmbed(ctx, currentTrack, musicManager.getCurrentAudioPlayerNum())
                    .setAuthor(title, null, ctx.event().getAuthor().getEffectiveAvatarUrl())
                    .build()
            ).queue();
        } else {
            // No track is currently playing
            MessageResources.embedDelete(
                ctx.event().getChannel(),
                10,
                EmbedResources.error("No song is currently playing!", "A song must be playing to view its information.", ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName())
            );
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "nowplaying";
    }

    @Override
    public List<String> getAliases() {
        return List.of("np");
    }

    @Override
    public String getDescription() {
        return "Shows the current track's playing song.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    public static EmbedBuilder songInfoEmbed(CommandContext ctx, AudioTrack audioTrack, int trackNum) {
        AudioTrackInfo trackInfo = audioTrack.getInfo();
        Duration trackDuration = Duration.ofMillis(trackInfo.length);
        String trackLength = String.format("%02d:%02d:%02d",
            trackDuration.toHours(),
            trackDuration.toMinutesPart(),
            trackDuration.toSecondsPart());

        return EmbedResources.defaultResponse(
                MusicManager.MUSIC_COLOR,
                "▶️ Now playing:",
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
            .setTitle(trackInfo.title)
            .setFooter("Track " + trackNum + "  •  #" + ctx.event().getChannel().getName())
            .addField("Channel", trackInfo.author, true)
            .addField("Song Duration", trackLength, true)
            .addField("Position in queue", "Now playing", true);
    }
}
