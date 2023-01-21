package com.raikuman.troubleclub.radio.listener.handler;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PaginationButtonProvider;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.modals.manager.ModalInterface;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.botutilities.slashcommands.manager.SlashInterface;
import com.raikuman.troubleclub.radio.commands.bot.PlayBot;
import com.raikuman.troubleclub.radio.commands.help.Help;
import com.raikuman.troubleclub.radio.commands.help.selects.MusicSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.PlaylistSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.TrackSelect;
import com.raikuman.troubleclub.radio.commands.music.*;
import com.raikuman.troubleclub.radio.commands.other.ChangePrefix;
import com.raikuman.troubleclub.radio.commands.other.Changelog;
import com.raikuman.troubleclub.radio.commands.other.ToS;
import com.raikuman.troubleclub.radio.commands.other.trello.RequestFeature;
import com.raikuman.troubleclub.radio.commands.other.trello.SubmitBug;
import com.raikuman.troubleclub.radio.commands.playlist.*;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.CancelDeletePlaylist;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.ConfirmDeletePlaylist;
import com.raikuman.troubleclub.radio.commands.playlist.deleteplaylist.DeletePlaylist;
import com.raikuman.troubleclub.radio.commands.track.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, selects, slashes, and modals for the ListenerHandler
 *
 * @version 1.9 2023-20-01
 * @since 1.1
 */
public class InvokeInterfaceProvider {

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	public static List<CommandInterface> provideCommands() {
		return Arrays.asList(
			new Join(),
			new Leave(),
			new Play(),
			new PlayNow(),
			new PlayTop(),
			new NowPlaying(),
			new Queue(),
			new Pause(),
			new Resume(),
			new Clear(),
			new Stop(),
			new Skip(),
			new SkipTo(),
			new Repeat(),
			new Loop(),
			new Remove(),
			new Move(),
			new Rewind(),
			new Random(),
			new Shuffle(),
			new ShufflePlay(),
			new Prune(),
			new Volume(),

			new ChangeTrack(),
			new PlayingTracks(),
			new PauseTracks(),
			new ResumeTracks(),
			new ClearTracks(),
			new StopTracks(),
			new RepeatTracks(),
			new LoopTracks(),
			new RewindTracks(),
			new PruneTracks(),
			new VolumeTracks(),

			new Playlist(),
			new PlayPlaylist(),
			new ShufflePlayPlaylist(),
			new CreatePlaylist(),
			new RenamePlaylist(),
			new DeletePlaylist(),

			new ChangePrefix(),
			new ToS(),
			new Changelog()
		);
	}

	/**
	 * Returns an array of bot commands
	 * @return The array of bot commands
	 */
	public static List<CommandInterface> provideBotCommands() {
		return Arrays.asList(
			new PlayBot()
		);
	}

	/**
	 * Returns all button interfaces
	 * @return The list of button interfaces
	 */
	public static List<ButtonInterface> provideButtons() {
		List<ButtonInterface> buttonInterfaces = new ArrayList<>();

		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new Queue()));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new Help()));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new MusicSelect(provideCommands())));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new TrackSelect(provideCommands())));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new PlaylistSelect(provideCommands())));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new OtherSelect(provideCommands())));
		buttonInterfaces.addAll(List.of(new CancelDeletePlaylist(), new ConfirmDeletePlaylist()));

		return buttonInterfaces;
	}

	/**
	 * Returns all select interfaces
	 * @return The list of select interfaces
	 */
	public static List<SelectInterface> provideSelects() {
		List<SelectInterface> selectInterfaces = new ArrayList<>();
		selectInterfaces.add(new MusicSelect(provideCommands()));
		selectInterfaces.add(new TrackSelect(provideCommands()));
		selectInterfaces.add(new PlaylistSelect(provideCommands()));
		selectInterfaces.add(new OtherSelect(provideCommands()));

		return selectInterfaces;
	}

	/**
	 * Returns all slash interfaces
	 * @return The list of slash interfaces
	 */
	public static List<SlashInterface> provideSlashes() {
		List<SlashInterface> slashInterfaces = new ArrayList<>();
		slashInterfaces.add(new Help());
		slashInterfaces.add(new SubmitBug());
		slashInterfaces.add(new RequestFeature());

		return slashInterfaces;
	}

	/**
	 * Returns all modal interfaces
	 * @return The list of modal interfaces
	 */
	public static List<ModalInterface> provideModals() {
		List<ModalInterface> modalInterfaces = new ArrayList<>();
		modalInterfaces.add(new SubmitBug());
		modalInterfaces.add(new RequestFeature());

		return modalInterfaces;
	}
}