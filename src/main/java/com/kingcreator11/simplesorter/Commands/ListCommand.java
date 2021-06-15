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

import java.util.List;

import com.kingcreator11.simplesorter.SimpleSorter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The list sub command
 */
public class ListCommand extends SubCommand {

	/**
	 * Creates a new list command instance
	 * @param plugin
	 */
	public ListCommand(SimpleSorter plugin) {
		super(plugin, new String[] {"simplesorter.usage"}, SubCommandType.argString);
	}

	/**
	 * Executes the sub command
	 * @param sender The sender of the command
	 * @param argument The argument of the command
	 */
	@Override
	protected void executeCommand(CommandSender sender, String argument) {
		
		if (!argument.isEmpty()) {
			if (!sender.hasPermission("simplesorter.admin")) {
				sender.sendMessage("§cInsufficient Privileges to use this command");
				return;
			}

			OfflinePlayer player = Bukkit.getOfflinePlayer(argument);
			
			if (player == null || !player.hasPlayedBefore()) {
				sender.sendMessage("§cPlayer not found, did you mispell the ign?");
				return;
			}

			String playerUUID = player.getUniqueId().toString();
			List<String> sorters = this.plugin.sorterManager.listSorters(playerUUID);

			printSorters(sender, sorters);

			return;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cOnly players may use this command");
			return;
		}

		Player player = (Player) sender;
		printSorters(sender, this.plugin.sorterManager.listSorters(player.getUniqueId().toString()));
	}

	private void printSorters(CommandSender sender, List<String> sorters) {
		if (sorters == null) {
			sender.sendMessage("§cWas unable to list the sorters due to technical reasons");
			return;
		}

		String message = "§cCurrently Owned Sorters:\n§2";

		for (String sorter : sorters) {
			message += sorter;
			if (!sorter.equals(sorters.get(sorters.size() - 1)))
				message += ", ";
		}

		sender.sendMessage(message);
	}
}