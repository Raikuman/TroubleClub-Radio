package com.raikuman.troubleclub.radio.invoke.playlist.createplaylist;

import com.raikuman.botutilities.invocation.type.ModalComponent;
import com.raikuman.botutilities.utilities.EmbedResources;
import com.raikuman.troubleclub.radio.database.PlaylistDatabaseHandler;
import com.raikuman.troubleclub.radio.music.manager.GuildMusicManager;
import com.raikuman.troubleclub.radio.music.manager.MusicManager;
import com.raikuman.troubleclub.radio.music.manager.TrackScheduler;
import com.raikuman.troubleclub.radio.music.playlist.PlaylistUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CreatePlaylistQueueModal extends ModalComponent {

    @Override
    public void handle(ModalInteractionEvent ctx) {
        if (ctx.getGuild() == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get the guild from your interaction.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        Message message = ctx.getMessage();
        if (message == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get your original message.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        // Retrieve name
        ModalMapping nameMapping = ctx.getValue("name");
        if (nameMapping == null) {
            // Update embed with error
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create your cassette!",
                "An error occurred while trying to get the name of your cassette.",
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        String name = nameMapping.getAsString();

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
                ctx.getChannel(), ctx.getUser()).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
            return;
        }

        // Encode all songs
        List<String> encodedTracks = new ArrayList<>();
        long playlistLength = 0L;
        for (AudioTrack audioTrack : audioTracks) {
            encodedTracks.add(musicManager.encodeTrack(audioTrack));
            playlistLength += audioTrack.getDuration();
        }

        boolean playlistCreated = PlaylistDatabaseHandler.addPlaylist(
            new com.raikuman.troubleclub.radio.music.playlist.Playlist(
                name,
                encodedTracks.size(),
                ctx.getUser()),
            encodedTracks);

        if (playlistCreated) {
            ctx.editMessageEmbeds(PlaylistUtils.getPlaylistInfoEmbed(
                ctx.getChannel(),
                ctx.getUser(),
                "\uD83D\uDCFC Created Cassette!",
                name,
                encodedTracks.size(),
                playlistLength
            ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
        } else {
            ctx.editMessageEmbeds(EmbedResources.error(
                "Could not create cassette!",
                "Could not add cassette to database.",
                ctx.getChannel(), ctx.getUser()
            ).build()).setComponents().delay(Duration.ofSeconds(10)).flatMap(InteractionHook::deleteOriginal).queue();
        }
    }

    @Override
    public String getInvoke() {
        return "createplaylistlinkmodal";
    }

    @Override
    public Modal getModal() {
        TextInput name = TextInput.create("name", "Name", TextInputStyle.SHORT)
            .setMinLength(3)
            .setMaxLength(20).build();

        return Modal.create(getInvoke(), "Record Cassette with Queue")
            .addComponents(ActionRow.of(name)).build();
    }
}