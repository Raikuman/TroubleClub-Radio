package com.raikuman.troubleclub.radio.invoke.track;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.music.MusicDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.category.Track;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class VolumeTracks extends Command {

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

        if (ctx.args().isEmpty()) {
            defaultHandle(ctx);
            ctx.event().getMessage().delete().queue();
        } else if (ctx.args().size() == 1) {
            // Check if second arg is a number between 1 and 100 (inclusive)
            int volume;
            try {
                volume = Integer.parseInt(ctx.args().get(0));
            } catch (NumberFormatException e) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
                return;
            }

            if (volume >= 1 && volume <= 100) {
                GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());

                for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
                    MusicDatabaseHandler.setVolume(ctx.event().getGuild(), i, volume);
                    musicManager.getAudioPlayer(i).setVolume((int) Math.ceil(volume / GuildMusicManager.REDUCE_VOLUME));
                }

                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    EmbedResources.defaultResponse(
                        MusicManager.TRACK_COLOR,
                        "\uD83D\uDD0A Set all tracks' volume to " + volume,
                        "",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor()));

                ctx.event().getMessage().delete().queue();
            } else {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            }
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
        }
    }

    @Override
    public String getInvoke() {
        return "volumetracks";
    }

    @Override
    public List<String> getAliases() {
        return List.of("vt", "volumet");
    }

    @Override
    public String getUsage() {
        return "(<volume 1-100>)";
    }

    @Override
    public String getDescription() {
        return "Shows all tracks' volumes, or set the volume of all tracks.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Track());
    }

    private void defaultHandle(CommandContext ctx) {
        StringBuilder volumeBuilder = new StringBuilder();

        for (int i = 1; i < GuildMusicManager.MAX_AUDIO_PLAYERS + 1; i++) {
            if (i != 1) {
                volumeBuilder.append("\n\n");
            }

            volumeBuilder
                .append("**Track ")
                .append(i)
                .append("**: `")
                .append(MusicDatabaseHandler.getVolume(ctx.event().getGuild(), i))
                .append("%`");
        }

        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.TRACK_COLOR,
                "\uD83D\uDD0A Track Volumes",
                volumeBuilder.toString(),
                ctx.event().getChannel(),
                ctx.event().getAuthor()));
    }
}
