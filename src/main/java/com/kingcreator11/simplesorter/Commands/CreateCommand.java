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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

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

		if (!(sender instanceof Player)) {
			sender.sendMessage("§cOnly players may use this command");
			return;
		}

		Player player = (Player) sender;
		String playerUUID = player.getUniqueId().toString();
		List<String> sorters = this.plugin.sorterManager.listSorters(playerUUID);

		if (sender.hasPermission("simplesorter.count")) {
			int highest = 0;

			// Get highest permission in `simplesorter.count.n`
			for (PermissionAttachmentInfo info : sender.getEffectivePermissions()) {
				if (!info.getValue())
					continue;
				
				if (info.getPermission().contains("simplesorter.count")) {
					try {
						String perm = info.getPermission();
						String[] sections = perm.split("\\.");
						int value = Integer.parseInt(sections[sections.length - 1]);
						if (value > highest) highest = value;
					}
					catch (NumberFormatException e) {
						continue;
					}
				}
			}

			if (sorters.size() >= highest) {
				sender.sendMessage("§cYou may only have up to "+highest+" sorters. You currently have "+sorters.size());
				return;
			}
		}

		// Name validation
		if (argument.length() > 30 || argument.length() < 1) {
			sender.sendMessage("§cThe name provided must be between 1 and 30 characters in length");
			return;
		}

		if (!argument.matches("^[a-zA-Z0-9_]+$")) {
			sender.sendMessage("§cThe name provided may only use english letters, numbers, and underscores.");
			return;
		}

		boolean sorterExists = false;
		for (String sorter : sorters)
			if (sorter.equals(argument))
				sorterExists = true;
		
		if (sorterExists) {
			sender.sendMessage("§cYou already own a sorter with the same name. Please choose a different name.");
			return;
		}

		// Name is valid!
		String response = this.plugin.sorterManager.createSorter(argument, playerUUID);
		if (response == null) {
			sender.sendMessage("§2Successfully created new sorter!");
		}
		else {
			sender.sendMessage("§c"+response);
		}
	}
}