package com.raikuman.troubleclub.radio.listener;

import com.raikuman.botutilities.buttons.manager.ButtonInterface;
import com.raikuman.botutilities.buttons.pagination.manager.PaginationButtonProvider;
import com.raikuman.botutilities.commands.manager.CommandInterface;
import com.raikuman.botutilities.selectmenus.manager.SelectInterface;
import com.raikuman.troubleclub.radio.commands.help.Help;
import com.raikuman.troubleclub.radio.commands.help.selects.MusicSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.OtherSelect;
import com.raikuman.troubleclub.radio.commands.help.selects.TrackSelect;
import com.raikuman.troubleclub.radio.commands.music.*;
import com.raikuman.troubleclub.radio.commands.other.Changelog;
import com.raikuman.troubleclub.radio.commands.other.ToS;
import com.raikuman.troubleclub.radio.commands.track.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides commands, buttons, and selects for the ListenerHandler
 *
 * @version 1.0 2022-09-07
 * @since 1.1
 */
public class InvokeInterfaceProvider {

	/**
	 * Returns all command interfaces
	 * @return The list of command interfaces
	 */
	public static List<CommandInterface> provideCommands() {
		List<CommandInterface> commands = new ArrayList<>(getCommands());
		commands.add(new Help());

		return commands;
	}

	/**
	 * Returns an array of commands
	 * @return The array of commands
	 */
	public static List<CommandInterface> getCommands() {
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
			new Move(),
			new Rewind(),
			new Random(),
			new Shuffle(),
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

			new ToS(),
			new Changelog()
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
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new MusicSelect(getCommands())));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new TrackSelect(getCommands())));
		buttonInterfaces.addAll(PaginationButtonProvider.provideButtons(new OtherSelect(getCommands())));

		return buttonInterfaces;
	}

	/**
	 * Returns all select interfaces
	 * @return The list of select interfaces
	 */
	public static List<SelectInterface> provideSelects() {
		List<SelectInterface> selectInterfaces = new ArrayList<>();
		selectInterfaces.add(new MusicSelect(getCommands()));
		selectInterfaces.add(new TrackSelect(getCommands()));
		selectInterfaces.add(new OtherSelect(getCommands()));

		return selectInterfaces;
	}
}
