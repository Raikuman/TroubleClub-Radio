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
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.ArrayList;
import java.util.List;

public class Remove extends Command {

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

        if (ctx.args().isEmpty()) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Get first arg
        String firstArg = ctx.args().get(0);
        int songNum;
        try {
            songNum = Integer.parseInt(firstArg);
        } catch (NumberFormatException e) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Check if song num is in range of queue
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();
        if (songNum < 0 || songNum > trackScheduler.queue.size()) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "\uD83D\uDDD1️ Could not remove your song!",
                        "Your song number `" + songNum + "` is not in range within the queue of `" + trackScheduler.queue.size() + "`!",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor())
                    .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
        } else {
            // Remove song
            List<AudioTrack> queueTracks = new ArrayList<>();
            trackScheduler.queue.drainTo(queueTracks);
            AudioTrack removedTrack = queueTracks.get(songNum - 1).makeClone();
            queueTracks.remove(songNum - 1);

            for (AudioTrack audioTrack : queueTracks) {
                trackScheduler.queue.offer(audioTrack);
            }

            EmbedBuilder embedBuilder = NowPlaying.songInfoEmbed(ctx, removedTrack, musicManager.getCurrentAudioPlayerNum())
                .setAuthor("\uD83D\uDDD1️ Removed song from queue:", null, ctx.event().getAuthor().getEffectiveAvatarUrl());

            List<MessageEmbed.Field> fields = embedBuilder.getFields();
            fields.remove(fields.size() - 1);
            embedBuilder.clearFields();

            for (MessageEmbed.Field field : fields) {
                embedBuilder.addField(field);
            }

            MessageResources.embedDelete(ctx.event().getChannel(), 10, embedBuilder);
            ctx.event().getMessage().delete().queue();
        }
    }

    @Override
    public String getInvoke() {
        return "remove";
    }

    @Override
    public List<String> getAliases() {
        return List.of("re");
    }

    @Override
    public String getUsage() {
        return "<song #>";
    }

    @Override
    public String getDescription() {
        return "Removes a song from the current track's queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }
}
