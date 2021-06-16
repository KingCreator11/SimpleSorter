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
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;

/**
 * Represents a single sub command
 */
public abstract class SubCommand extends SimpleSorterBase {

	/**
	 * Represents what type of arguments this sub command requires when handling the command
	 */
	protected enum SubCommandType {
		argsList, argString
	}

	/**
	 * Represents what type of sub command this is
	 */
	private SubCommandType subCommandType;

	/**
	 * A list of all required permissions to use the command
	 * A sender must have all the permissions in this list to use the command
	 */
	private String[] requiredPermissions;

	/**
	 * Creates a new sub command
	 * @param plugin A pointer to the plugin instance
	 * @param requiredPermissions The required permissions to use this sub command
	 * @param subCommandType The type of sub command this is
	 */
	public SubCommand(SimpleSorter plugin, String[] requiredPermissions, SubCommandType subCommandType) {
		super(plugin);
		this.requiredPermissions = requiredPermissions;
		this.subCommandType = subCommandType;
	}

	/**
	 * Executes the sub command
	 * @param args The arguments list (Discluding the name of the subcommand itself)
	 * @param sender The sender of the command
	 */
	public void executeCommand(String[] args, CommandSender sender) {
		switch (this.subCommandType) {
			case argString:
				this.executeCommand(sender, String.join(" ", args));
				break;
			case argsList:
				this.executeCommand(sender, args);
				break;
		}
	}

	/**
	 * Checks whether or not a sender has permissions to use this sub command
	 * @param sender The sender to check for
	 * @return
	 */
	public boolean hasPerms(CommandSender sender) {
		for (int i = 0; i < this.requiredPermissions.length; i++)
			if (!sender.hasPermission(this.requiredPermissions[i]))
				return false;

		return true;
	}

	/**
	 * Checks whether or not a block is a valid container for sorting
	 * @param block
	 * @return
	 */
	protected boolean blockIsSorterContainer(Block block) {
		BlockState state = block.getState();
		return state instanceof Chest || state instanceof Barrel;
	}

	/**
	 * Override this - This is called when the sub command is used.
	 * This method in specific is called if you set the SubCommandType to argString
	 * @param sender The sender of the command
	 * @param argument The arguments required 
	 */
	protected void executeCommand(CommandSender sender, String argument) {}

	/**
	 * Override this - This is called when the sub command is used.
	 * This method in specific is called if you set the SubCommandType to argsList
	 * @param sender The sender of the command
	 * @param argument The arguments required 
	 */
	protected void executeCommand(CommandSender sender, String[] args) {}
}