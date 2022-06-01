/*
   Copyright 2021 KingCreator11

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.kingcreator11.simplesorter.Commands;

import java.util.ArrayList;
import java.util.List;

import com.kingcreator11.simplesorter.SimpleSorter;

import org.bukkit.command.CommandSender;

/**
 * The help sub command
 */
public class HelpCommand extends SubCommand {

	/**
	 * A helper class to provide the info for a single command
	 */
	private class CommandInfo {

		/**
		 * How the command is to be used
		 */
		public String usage;

		/**
		 * The description of the command
		 */
		public String description;

		/**
		 * The permission required to use this command
		 */
		public String permission = "simplesorter.usage";

		/**
		 * Creates a new command info instance
		 * @param usage How the command is to be used
		 * @param description The description of the command
		 */
		public CommandInfo(String usage, String description) {
			this.usage = usage;
			this.description = description;
		}

		/**
		 * Creates a new command info instance
		 * @param usage How the command is to be used
		 * @param description The description of the command
		 * @param permission The permission required to use this command
		 */
		public CommandInfo(String usage, String description, String permission) {
			this.usage = usage;
			this.description = description;
			this.permission = permission;
		}
	}

	private CommandInfo[] commandInfo = {
		new CommandInfo("help §9<pagenumber>", "Pulls up this help page"),
		new CommandInfo("create §9<name>", "Creates a new simple sorter"),
		new CommandInfo("delete §9<name>", "Deletes a simple sorter"),
		new CommandInfo("delete §9<ign>§b:§9<name>", "Deletes a player's simple sorter", "simplesorter.admin"),
		new CommandInfo("list", "Lists the simple sorters you own"),
		new CommandInfo("list §9<ign>", "Lists a player's simple sorters", "simplesorter.admin"),
		new CommandInfo("setinput §9<name>", "Sets an input chest for a sorter"),
		new CommandInfo("removeinput", "Removes an input chest for a sorter"),
		new CommandInfo("setinvalid §9<name>", "Sets an invalid items chest for a sorter"),
		new CommandInfo("removeinvalid", "Removes an invalid items chest for a sorter"),
		new CommandInfo("setshulkerinput §9<name>", "Converts a sorter to a shulker based sorter and sets the empty shulker input chest", "simplesorter.shulkers"),
		new CommandInfo("removeshulkerinput", "Converts a sorter back to a normal item sorter and removes the shulker input chest"),
		new CommandInfo("sort §9<name>", "Creates a sorter for the held item"),
		new CommandInfo("removesorter", "Removes a sorter chest"),
		new CommandInfo("autosort <name> <interval>", "turns autosorting on/off for an input chest and runs every <interval> seconds")
	};

	/**
	 * Creates a new Help command instance
	 * @param plugin
	 */
	public HelpCommand(SimpleSorter plugin) {
		super(plugin, new String[] {"simplesorter.usage"}, SubCommandType.argString);
	}

	/**
	 * Executes the sub command
	 * @param sender The sender of the command
	 * @param argument The argument of the command
	 */
	@Override
	protected void executeCommand(CommandSender sender, String argument) {
		String message = "§6>------ §cSimple Sorter Help §6------<\n";
		int page = 1;
		List<CommandInfo> infoSubset = new ArrayList<>();

		for (int i = 0; i < commandInfo.length; i++)
			if (sender.hasPermission(commandInfo[i].permission))
				infoSubset.add(commandInfo[i]);

		int numPages = (int) Math.ceil((double) infoSubset.size() / 5.0);

		try {
			page = Integer.parseInt(argument);
			if (page > numPages) page = numPages;
			if (page < 1) page = 1;
		}
		catch (NumberFormatException e) {
			// Do nothing - just give the first page of the help
		}

		for (int i = (page-1)*5; i < (page*5 > infoSubset.size() ? infoSubset.size() : page*5); i++) {
			CommandInfo cmdInfo = infoSubset.get(i);
			message += "§3/ss "+cmdInfo.usage + " §7: §f"+cmdInfo.description + "\n";
		}

		message += "§6>------ §cPage "+page+" of "+numPages+" §6------<";
		sender.sendMessage(message);
	}
}