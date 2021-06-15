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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 * Handles the basic commands of /ss and /simplesorter
 */
public class CommandHandler extends SimpleSorterBase implements CommandExecutor, TabCompleter {

	/**
	 * The map of sub commands
	 */
	private Map<String, SubCommand> subCommandMap = new HashMap<>();

	/**
	 * Creates a new command handler
	 * @param plugin Pointer to simple sorter instance
	 */
	public CommandHandler(SimpleSorter plugin) {
		super(plugin);
	}

	/**
	 * Adds a sub command to the command handler
	 * @param subCommand The sub command to add
	 * @param commandHandler The handler for the sub command
	 */
	public void addSubCommand(String subCommand, SubCommand commandHandler) {
		subCommandMap.put(subCommand, commandHandler);
	}

	/**
	 * Removes a sub command to the command handler
	 * @param subCommand The sub command to remove
	 */
	public void removeSubCommand(String subCommand, SubCommand commandHandler) {
		subCommandMap.remove(subCommand);
	}

	/**
	 * Called when tab completion for commands is required
	 */
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length > 1) return null;

		List<String> completions = new ArrayList<>();

		String searchKey = args.length > 0 ? args[0] : "";

		for (String subCommand : this.subCommandMap.keySet())
			if (subCommand.contains(searchKey) && this.subCommandMap.get(subCommand).hasPerms(sender))
				completions.add(subCommand);

		return completions;
	}

	/**
	 * Called when a command is used
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("§4No Sub Command Given - use /ss help for a list of commands");
			return true;
		}

		SubCommand subCommand = subCommandMap.get(args[0]);

		if (subCommand == null) {
			sender.sendMessage("§4Command not found - use /ss help for a list of commands");
			return true;
		}

		if (!subCommand.hasPerms(sender)) {
			sender.sendMessage("§4Insufficient Privileges");
			return true;
		}

		try {
			subCommand.executeCommand(args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[]{}, sender);
		}
		catch (Exception e) {
			sender.sendMessage("§4Whoops something went wrong! Try a different command and please inform the developers regarding the console output!");
			e.printStackTrace();
		}
		
		return true;
	}
}