package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.invoke.category.Playlist;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.playerhandler.music.PlayHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Play extends Command {

    @Override
    public void handle(CommandContext ctx) {
        if (MusicChecking.setup(
            ctx.event().getGuild(),
            ctx.event().getChannel(),
            ctx.event().getMessage(),
            ctx.event().getMember())
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, false)
            .checkLacksPermission(true)
            .check()) {
            return;
        }

        // Handle command
        if (ctx.args().isEmpty()) {
            defaultHandle(ctx);
        } else {
            playMusic(ctx);
        }
    }

    @Override
    public String getInvoke() {
        return "play";
    }

    @Override
    public List<String> getAliases() {
        return List.of("p");
    }

    @Override
    public String getUsage() {
        return "(<link/search>)";
    }

    @Override
    public String getDescription() {
        return "Plays a song or playlist from a link, or search for a song to play on the current track. Also resumes the current " +
            "track if paused provided no link.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music(), new Playlist());
    }

    private void defaultHandle(CommandContext ctx) {
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        AudioPlayer audioPlayer = musicManager.getCurrentAudioPlayer();
        AudioTrack currentTrack = musicManager.getCurrentAudioPlayer().getPlayingTrack();
        if (currentTrack != null) {
            if (audioPlayer.isPaused()) {
                audioPlayer.setPaused(false);

                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    Pause.resumingSong(musicManager, ctx));
            } else {
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    songAlreadyPlaying(ctx));
            }

            ctx.event().getMessage().delete().queue();
        } else {
            MessageResources.embedDelete(ctx.event().getChannel(), 10,
                Pause.noSong(musicManager, ctx, "▶️ No song to resume!"));
        }
    }

    private void playMusic(CommandContext ctx) {
        // Connect
        MusicManager.getInstance().connect(ctx);

        String link = String.join(" ", ctx.args());
        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        MusicManager.getInstance().play(new PlayHandler(
            ctx.event(), link));
    }

    public static boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static EmbedBuilder songAlreadyPlaying(CommandContext ctx) {
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());

        return NowPlaying.songInfoEmbed(
                ctx,
                musicManager.getCurrentAudioPlayer().getPlayingTrack(),
                musicManager.getCurrentAudioPlayerNum())
            .setAuthor("▶️ Song is already playing:", null, ctx.event().getAuthor().getEffectiveAvatarUrl());
    }
}
