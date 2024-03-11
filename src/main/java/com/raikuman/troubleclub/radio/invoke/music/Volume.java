package com.raikuman.troubleclub.radio.invoke.music;

import com.raikuman.botutilities.invocation.Category;
import com.raikuman.botutilities.invocation.context.CommandContext;
import com.raikuman.botutilities.invocation.type.Command;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.botutilities.utilities.MessageResources;
import com.raikuman.troubleclub.radio.database.music.MusicDatabaseHandler;
import com.raikuman.troubleclub.radio.invoke.category.Music;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;

import java.util.List;

public class Volume extends Command {
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
        } else if (ctx.args().size() == 2) {
            if (!updateVolume(ctx, false)) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            } else {
                ctx.event().getMessage().delete().queue();
            }
        } else if (ctx.args().size() == 1) {
            if (!updateVolume(ctx, true)) {
                MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                    EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
            } else {
                ctx.event().getMessage().delete().queue();
            }
        } else {
            MessageResources.embedReplyDelete(ctx.event().getMessage(), 10, true,
                EmbedResources.incorrectUsage(getInvoke(), getUsage(), ctx.event().getChannel()));
        }
    }

    @Override
    public String getInvoke() {
        return "volume";
    }

    @Override
    public List<String> getAliases() {
        return List.of("v");
    }

    @Override
    public String getUsage() {
        return "(<volume 1-100>)";
    }

    @Override
    public String getDescription() {
        return "Shows the current track's volume, or set the current track's volume.";
    }

    @Override
    public List<Category> getCategories() {
        return List.of(new Music());
    }

    private void defaultHandle(CommandContext ctx) {
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());

        // Send volume embed
        MessageResources.embedDelete(ctx.event().getChannel(), 10,
            EmbedResources.defaultResponse(
                MusicManager.MUSIC_COLOR,
                "\uD83D\uDD0A Track `" + musicManager.getCurrentAudioPlayerNum() + "` volume is `" +
                    musicManager.getCurrentAudioPlayer().getVolume() + "`",
                "",
                ctx.event().getChannel(),
                ctx.event().getAuthor())
                .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));
    }

    private boolean updateVolume(CommandContext ctx, boolean currentTrack) {
        boolean updated = false;
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.event().getGuild());

        // Check if first arg is a number between 1 and 3 (inclusive)
        int trackNum;
        if (currentTrack) {
            trackNum = musicManager.getCurrentAudioPlayerNum();
        } else {
            try {
                trackNum = Integer.parseInt(ctx.args().get(0));
            } catch (NumberFormatException e) {
                trackNum = 0;
            }
        }

        if (trackNum >= 1 && trackNum <= 3) {
            // Check if second arg is a number between 1 and 100 (inclusive)
            int volume;
            try {
                if (currentTrack) {
                    volume = Integer.parseInt(ctx.args().get(0));
                } else {
                    volume = Integer.parseInt(ctx.args().get(1));
                }
            } catch (NumberFormatException e) {
                volume= 0;
            }

            if (volume >= 1 && volume <= 100) {
                MusicDatabaseHandler.setVolume(ctx.event().getGuild(), trackNum, volume);
                musicManager.getAudioPlayer(trackNum).setVolume(volume);

                // Send volume embed
                MessageResources.embedDelete(ctx.event().getChannel(), 10,
                    EmbedResources.defaultResponse(
                        MusicManager.MUSIC_COLOR,
                        "\uD83D\uDD0A Set track " + musicManager.getCurrentAudioPlayerNum() + " volume to " + volume,
                        "",
                        ctx.event().getChannel(),
                        ctx.event().getAuthor())
                        .setFooter("Track " + musicManager.getCurrentAudioPlayerNum() + "  •  #" + ctx.event().getChannel().getName()));

                updated = true;
            }
        }

        return updated;
    }
}
