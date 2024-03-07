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

public class Move extends Command {

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

        boolean incorrectUsage = false;
        if (ctx.args().isEmpty()) {
            incorrectUsage = true;
        } else {
            if (ctx.args().size() == 1) {
                int songNum;
                try {
                    songNum = Integer.parseInt(ctx.args().get(0));
                    if (songNum > 0) {
                        moveSong(ctx, songNum, 1);
                    } else {
                        incorrectUsage = true;
                    }
                } catch (NumberFormatException e) {
                    incorrectUsage = true;
                }
            } else if (ctx.args().size() == 2) {
                int songNum, positionNum;
                try {
                    songNum = Integer.parseInt(ctx.args().get(0));
                    positionNum = Integer.parseInt(ctx.args().get(1));
                    if (songNum > 0 && positionNum > 0) {
                        moveSong(ctx, songNum, positionNum);
                    } else {
                        incorrectUsage = true;
                    }
                } catch (NumberFormatException e) {
                    incorrectUsage = true;
                }
            } else {
                incorrectUsage = true;
            }
        }

        if (incorrectUsage) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
        }
    }

    @Override
    public String getInvoke() {
        return "move";
    }

    @Override
    public List<String> getAliases() {
        return List.of("m");
    }

    @Override
    public String getUsage() {
        return "<song #> (<position #>)";
    }

    @Override
    public String getDescription() {
        return "Moves a song from the current track's queue to a different location in the queue.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    private void moveSong(CommandContext ctx, int songNum, int positionNum) {
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();

        if (positionNum > trackScheduler.queue.size()) {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            return;
        }

        // Handle moving song
        List<AudioTrack> queueTracks = new ArrayList<>();
        trackScheduler.queue.drainTo(queueTracks);
        AudioTrack moveTrack = queueTracks.remove(songNum - 1);

        int currentPos = 1;
        for (AudioTrack track : queueTracks) {
            if (currentPos == positionNum) {
                trackScheduler.queue.offer(moveTrack);
            }

            trackScheduler.queue.offer(track);
            currentPos++;
        }

        EmbedBuilder moveEmbed = NowPlaying.songInfoEmbed(ctx, moveTrack, musicManager.getCurrentAudioPlayerNum())
            .setAuthor("↘️ Moving song:", null, ctx.event().getAuthor().getEffectiveAvatarUrl())
            .setDescription("Moved from position `" + songNum + "` to position `" + positionNum + "`");

        List<MessageEmbed.Field> fields = moveEmbed.getFields();
        fields.set(2, new MessageEmbed.Field("Position in queue", String.valueOf(positionNum), true));
        moveEmbed.clearFields();
        for (MessageEmbed.Field field : fields) {
            moveEmbed.addField(field);
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10, moveEmbed);
        ctx.event().getMessage().delete().queue();
    }
}
