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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The delete sub command
 */
public class DeleteCommand extends SubCommand {

	/**
	 * Creates a new delete command instance
	 * @param plugin
	 */
	public DeleteCommand(SimpleSorter plugin) {
		super(plugin, new String[] {"simplesorter.usage"}, SubCommandType.argString);
	}

	/**
	 * Executes the sub command
	 * @param sender The sender of the command
	 * @param argument The argument of the command
	 */
	@Override
	protected void executeCommand(CommandSender sender, String argument) {
		
		if (argument.contains(":")) {

			if (!sender.hasPermission("simplesorter.admin")) {
				sender.sendMessage("§cInsufficient Privileges to use this command");
				return;
			}

			String[] args = argument.split("\\:");
			if (args.length != 2 || args[0] == null || args[1] == null || args[0].equals("") || args[1].equals("")) {
				sender.sendMessage("§cInvalid argument, usage is /ss delete <ign>:<name>");
				return;
			}

			String ign = args[0];
			String name = args[1];

			OfflinePlayer player = Bukkit.getOfflinePlayer(ign);
			
			if (player == null || !player.hasPlayedBefore()) {
				sender.sendMessage("§cPlayer not found, did you mispell the ign?");
				return;
			}

			String playerUUID = player.getUniqueId().toString();

			if (this.plugin.sorterManager.deleteSorter(name, playerUUID)) {
				sender.sendMessage("§2Successfully deleted the sorter!");
			}
			else {
				sender.sendMessage("§cWas unable to delete the sorter for technical reasons");
			}

			return;
		}

		if (argument.equals("")) {
			sender.sendMessage("§cPlease provide the name of the sorter you wish to delete");
			return;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cOnly players may use this command.");
			return;
		}

		Player player = (Player) sender;

		if (this.plugin.sorterManager.deleteSorter(argument, player.getUniqueId().toString())) {
			sender.sendMessage("§2Successfully deleted the sorter!");
		}
		else {
			sender.sendMessage("§cWas unable to delete the sorter for technical reasons");
		}
	}
}