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

import java.util.List;

public class Rewind extends Command {

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
            audioPlayer.startTrack(currentTrack.makeClone(), false);

            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                NowPlaying.songInfoEmbed(ctx, currentTrack, musicManager.getCurrentAudioPlayerNum())
                    .setAuthor("⏪ Rewinding:", null, ctx.event().getAuthor().getEffectiveAvatarUrl()));
        } else {
            // No track is currently playing
            MessageResources.embedDelete(
                ctx.event().getChannel(),
                10,
                EmbedResources.error("No song is currently playing!", "A song must be playing to rewind.", ctx.event().getChannel(),
                    ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName())
            );
        }

        ctx.event().getMessage().delete().queue();
    }

    @Override
    public String getInvoke() {
        return "rewind";
    }

    @Override
    public String getDescription() {
        return "Rewind the current track's playing song to the beginning.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
