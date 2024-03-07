package com.raikuman.troubleclub.radio.music.manager;

import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.musichandler.MusicHandler;
import com.raikuman.troubleclub.radio.music.playlist.Playlist;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MusicManager {

    private static MusicManager PLAYER_INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagerMap;
    private final AudioPlayerManager audioPlayerManager;
    public static final Color MUSIC_COLOR = Color.decode("#4287f5"), CASSETTE_COLOR = Color.decode("#801cba");

    public MusicManager() {
        this.musicManagerMap = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        // Check source of track
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static MusicManager getInstance() {
        if (PLAYER_INSTANCE == null) {
            PLAYER_INSTANCE = new MusicManager();
        }

        return PLAYER_INSTANCE;
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagerMap.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);

            // Update send handler using mixer
            guild.getAudioManager().setSendingHandler(guildMusicManager.getMixingSendHandler());
            return guildMusicManager;
        });
    }

    public void connect(CommandContext ctx) {
        AudioManager guildAudioManager = ctx.event().getGuild().getAudioManager();
        AudioChannelUnion audioChannel = MusicChecking.retrieveMemberVoiceChannel(ctx);

        if (audioChannel == null) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.error("Could not connect to your voice channel!",
                    "Please contact an administrator for help",
                    ctx.event().getChannel(), ctx.event().getAuthor()));
            return;
        }

        // Check if bot is already connected to member's channel
        Member self = guildAudioManager.getGuild().getSelfMember();
        if (self.getVoiceState() != null && self.getVoiceState().getChannel() != null &&
            self.getVoiceState().getChannel().equals(audioChannel)) {
            return;
        }

        guildAudioManager.openAudioConnection(audioChannel);
        guildAudioManager.setSelfDeafened(true);

    }

    public void play(MusicHandler musicHandler) {
        GuildMusicManager musicManager = getMusicManager(musicHandler.getGuild());
        this.audioPlayerManager.loadItemOrdered(
            musicManager,
            musicHandler.getUrl(),
            musicHandler.getResultHandler(musicManager));
    }

    public void play(MusicHandler musicHandler, Playlist playlist) {
        GuildMusicManager musicManager = getMusicManager(musicHandler.getGuild());
        this.audioPlayerManager.loadItemOrdered(
            musicManager,
            musicHandler.getUrl(),
            musicHandler.getResultHandler(musicManager, playlist));
    }

    private static EmbedBuilder getMusicEmbed(GuildMusicManager musicManager, MessageChannelUnion channel, User user, String method, String title,
                                              Color color, MessageEmbed.Field... fields) {
        EmbedBuilder embedBuilder = new EmbedBuilder()
            .setColor(color)
            .setAuthor(method, null, user.getAvatarUrl())
            .setTitle(title)
            .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + channel.getName())
            .setTimestamp(Instant.now());

        // Add fields
        for (MessageEmbed.Field field : fields) {
            embedBuilder.addField(field);
        }

        return embedBuilder;
    }

    public static EmbedBuilder getAudioTrackEmbed(GuildMusicManager musicManager, String method, MessageChannelUnion channel, User user, int queueSize,
                                                  AudioTrack audioTrack) {
        String nowPlaying;
        if (queueSize == 0) {
            nowPlaying = "Now playing";
        } else {
            nowPlaying = String.valueOf(queueSize);
        }

        AudioTrackInfo info = audioTrack.getInfo();
        return getMusicEmbed(musicManager, channel, user, method, info.title, MUSIC_COLOR,
            new MessageEmbed.Field("Channel", info.author, true),
            new MessageEmbed.Field("Duration", formatMilliseconds(audioTrack.getDuration()), true),
            new MessageEmbed.Field("Position in queue", nowPlaying, true)
        );
    }

    public static EmbedBuilder getPlaylistEmbed(GuildMusicManager musicManager, String method, MessageChannelUnion channel, User user, String playlistName,
                                                List<AudioTrack> audioTracks, boolean isCassette) {
        long playlistLength = 0L;
        for (AudioTrack track : audioTracks) {
            playlistLength += track.getDuration();
        }

        String playlistText;
        Color embedColor;
        if (isCassette) {
            playlistText = "cassette";
            embedColor = CASSETTE_COLOR;
        } else {
            playlistText = "playlist";
            embedColor = MUSIC_COLOR;
        }

        return getMusicEmbed(musicManager, channel, user, method, playlistName, embedColor,
            new MessageEmbed.Field("Songs in " + playlistText, String.valueOf(audioTracks.size()), true),
            new MessageEmbed.Field("Length of " + playlistText, formatMilliseconds(playlistLength), true)
        );
    }

    private static String formatMilliseconds(long timeInMillis) {
        return String.format("%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(timeInMillis),
            TimeUnit.MILLISECONDS.toMinutes(timeInMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeInMillis)),
            TimeUnit.MILLISECONDS.toSeconds(timeInMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillis))
        );
    }
}
