package com.raikuman.troubleclub.radio.invoke.playlist.createplaylist;

import com.raikuman.botutilities.invocation.type.ButtonComponent;
import com.raikuman.botutilities.invocation.type.ModalComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.music.MusicChecking;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CreateFromQueueButton extends ButtonComponent {

    @Override
    public void handle(ButtonInteractionEvent ctx) {
        if (MusicChecking.setup(
                ctx.getGuild(),
                ctx.getChannel(),
                ctx.getMessage(),
                ctx.getMember())
            .checkPrivateMessage(true)
            .checkMemberNotInVoiceChannel(true)
            .checkBotInDifferentVoiceChannel(true, true)
            .check()) {
            return;
        }

        if (ctx.getGuild() == null) {
            return;
        }

        // Retrieve all songs in track
        GuildMusicManager musicManager = MusicManager.getInstance().getMusicManager(ctx.getGuild());
        TrackScheduler trackScheduler = musicManager.getCurrentTrackScheduler();
        List<AudioTrack> audioTracks = new ArrayList<>();
        if (trackScheduler.audioPlayer.getPlayingTrack() != null) {
            audioTracks.add(trackScheduler.audioPlayer.getPlayingTrack());
        }
        audioTracks.addAll(trackScheduler.queue);

        if (audioTracks.isEmpty()) {
            ctx.editMessageEmbeds(EmbedResources.error(
                "No songs to create a cassette with!",
                "There must be a song playing or songs in queue to create a cassette.",
                ctx.getChannel(), ctx.getUser()
            ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        // Send modal
        ModalComponent modalComponent = new CreatePlaylistQueueModal();
        componentHandler.addModal(modalComponent);

        ctx.replyModal(modalComponent.getModal()).queue();
    }

    @Override
    public String getInvoke() {
        return "createfromqueuebutton";
    }

    @Override
    public Emoji displayEmoji() {
        return Emoji.fromFormatted("⏏️");
    }

    @Override
    public String displayLabel() {
        return "Use Queue";
    }

    @Override
    public ButtonStyle buttonStyle() {
        return ButtonStyle.PRIMARY;
    }
}
