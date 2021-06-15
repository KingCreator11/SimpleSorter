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

import com.kingcreator11.simplesorter.SimpleSorter;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The create sub command
 */
public class CreateCommand extends SubCommand {

	/**
	 * Creates a new create command instance
	 * @param plugin
	 */
	public CreateCommand(SimpleSorter plugin) {
		super(plugin, new String[] {"simplesorter.usage"}, SubCommandType.argString);
	}

	/**
	 * Executes the sub command
	 * @param sender The sender of the command
	 * @param argument The argument of the command
	 */
	@Override
	protected void executeCommand(CommandSender sender, String argument) {

		// Name validation
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cOnly players may use this command");
			return;
		}

		Player player = (Player) sender;

		if (argument.length() > 30 || argument.length() < 1) {
			sender.sendMessage("§cThe name provided must be between 1 and 30 characters in length");
			return;
		}

		if (!argument.matches("^[a-zA-Z0-9_]+$")) {
			sender.sendMessage("§cThe name provided may only use english letters, numbers, and underscores.");
			return;
		}

		// Name is valid!
		if (this.plugin.sorterManager.createSorter(argument, player.getUniqueId().toString())) {
			sender.sendMessage("§2Successfully created new sorter!");
		}
		else {
			sender.sendMessage("§cWe were unable to create the sorter due to a technical error");
		}
	}
}