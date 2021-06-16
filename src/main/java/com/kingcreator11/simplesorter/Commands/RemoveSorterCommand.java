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

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

/**
 * The remove sorter sub command
 */
public class RemoveSorterCommand extends SubCommand {

	/**
	 * Creates a new remove sorter command instance
	 * @param plugin
	 */
	public RemoveSorterCommand(SimpleSorter plugin) {
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
		RayTraceResult result = player.rayTraceBlocks(10);

		if (result == null) {
			sender.sendMessage("§cPlease point at a container to remove from the sorter");
			return;
		}

		Block block = result.getHitBlock();
		
		if (block == null || !this.blockIsSorterContainer(block)) {
			sender.sendMessage("§cPlease point at a container to remove from the sorter");
			return;
		}

		String dbresult = this.plugin.sorterContainerManager.removeSorterContainer(block.getLocation(), player.getUniqueId().toString());
		if (dbresult == null) {
			sender.sendMessage("§2Successfully removed the container from the sorter");
		}
		else {
			sender.sendMessage("§c"+dbresult);
		}
	}
}