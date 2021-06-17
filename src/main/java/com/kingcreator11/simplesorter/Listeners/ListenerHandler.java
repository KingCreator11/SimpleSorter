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

package com.kingcreator11.simplesorter.Listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;
import com.kingcreator11.simplesorter.Database.DBContainer;
import com.kingcreator11.simplesorter.SortingProcess.SortingProcess;

import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Handles all events for this plugin
 */
public class ListenerHandler extends SimpleSorterBase implements Listener {

	/**
	 * A map of all the currently on going sorting processes
	 * Maps the input container to the sorting process
	 */
	private Map<DBContainer, SortingProcessRunner> onGoingSorters = new HashMap<>();

	/**
	 * Begins a sorting process after a few moments and updates it
	 */
	private class SortingProcessRunner extends BukkitRunnable {

		/**
		 * The process used for sorting
		 */
		private SortingProcess process;

		/**
		 * The input container this process is mapped to
		 */
		DBContainer input;

		/**
		 * Creates a new sorting process runner
		 * @param plugin
		 * @param input
		 */
		public SortingProcessRunner(SimpleSorter plugin, DBContainer input, Inventory inputInv) {
			process = new SortingProcess(plugin, inputInv, input.sorterId);
			this.input = input;
		}

		/**
		 * Runs and updates the sorting process
		 */
		@Override
		public void run() {
			// Sort 1 stack of items each tick - very fast but will not create excessive lag
			process.update();
			
			if (process.completed()) {
				// Process completed! remove ourselves from the map
				onGoingSorters.remove(input);
				this.cancel();
			}
		}
	}

	/**
	 * Creates a new event handler
	 * @param plugin
	 */
	public ListenerHandler(SimpleSorter plugin) {
		super(plugin);
	}

	/**
	 * Event handler
	 * 
	 * @param event event
	 */
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {

		// Get both holders
		HumanEntity playerHolder = event.getWhoClicked();
		Block block = event.getInventory().getLocation().getBlock();
		boolean isDoubleChest = false;

		// Source must be a player and destination must be a chest/barrel
		if (!(playerHolder instanceof Player)) return;
		if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) return;
		if (event.getInventory() instanceof DoubleChestInventory) isDoubleChest = true;

		// Convert to player/block objs and get uuid
		Player player = (Player) playerHolder; 
		String playerUUID = player.getUniqueId().toString();
		
		// Does the player even have any input containers?
		if (!this.plugin.inputManager.inputLocations.containsKey(playerUUID)) return;

		// Loop through containers and check if this is one of them
		List<DBContainer> containers = this.plugin.inputManager.inputLocations.get(playerUUID);
		DBContainer input = null;

		// Not a double chest - normal looping through is fine
		if (!isDoubleChest) {
			for (DBContainer container : containers){
				if (container.location.equals(block.getLocation())) {
					input = container;
					break;
				}
			}
		}
		// Double chests need checking for both sides
		else {
			DoubleChest doubleChest = (DoubleChest) event.getInventory().getHolder();
			Chest left = (Chest) doubleChest.getLeftSide();
			Chest right = (Chest) doubleChest.getRightSide();
			for (DBContainer container : containers){
				if (container.location.equals(left.getLocation()) || container.location.equals(right.getLocation())) {
					input = container;
					break;
				}
			}
		}

		// The container in question isn't an input container
		if (input == null) return;

		// Check if there's already a sorting process occurring for this chest
		// [The sorting process can handle new items in middle of the sort]
		if (this.onGoingSorters.containsKey(input)) return;

		// All checks done - this chest is an input chest.
		player.sendMessage("§2Starting sorting process in 5 seconds");
		player.sendMessage("§6You can still add items - added items will also be sorted");

		// Create sorting process runner and run it
		SortingProcessRunner runner = new SortingProcessRunner(this.plugin, input, event.getInventory());
		this.onGoingSorters.put(input, runner);

		// Ideally 5 seconds after but can be delayed by lag
		runner.runTaskTimer(this.plugin, 5*20, 1);
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		removeBlock(event.getBlock());
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onBlockDamageEvent(BlockDamageEvent event) {
		removeBlock(event.getBlock());
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onBlockExplodeEvent(BlockExplodeEvent event) {
		removeBlock(event.getBlock());
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		for (Block block : event.blockList())
			removeBlock(block);
	}

	/**
	 * Removes a block
	 * @param block
	 */
	private void removeBlock(Block block) {
		if (!(block.getState() instanceof Barrel || block.getState() instanceof Chest))
			return;
		
		// The block is a barrel/chest
		this.plugin.sorterManager.removeAtCoords(block.getLocation());
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		this.plugin.inputManager.loadInputs(event.getPlayer().getUniqueId().toString());
	}

	/**
	 * Event handler
	 * @param event event
	 */
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		this.plugin.inputManager.unloadInputs(event.getPlayer().getUniqueId().toString());
	}
}