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

package com.kingcreator11.simplesorter.SortingProcess;

import java.util.ArrayList;
import java.util.List;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;
import com.kingcreator11.simplesorter.Database.DBContainer;
import com.kingcreator11.simplesorter.Database.SorterContainerManager.SorterContainer;

import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * Represents a single sorting process
 */
public class SortingProcess extends SimpleSorterBase {

	/**
	 * The input container inventory
	 */
	private Inventory inputInventory;

	/**
	 * Whether or not the process has completed
	 */
	private boolean processHasCompleted = false;

	/**
	 * a list of exempt indices from the sorting process
	 * [ie indices which have items not in the system when there's no invalid output chest]
	 */
	private List<Integer> exmptIndices = new ArrayList<>();

	/**
	 * A list of all sorter containers available
	 */
	private List<SorterContainer> sorterContainers;

	/***
	 * A list of all shulker inputs available
	 */
	private List<DBContainer> shulkerInputs;

	/**
	 * A list of all invalid outputs available
	 */
	private List<DBContainer> invalidOutputs;

	/**
	 * Creates a new sorting process
	 * @param plugin
	 * @param input
	 */
	public SortingProcess(SimpleSorter plugin, Inventory input, int sorterId) {
		super(plugin);
		this.inputInventory = input;

		sorterContainers = this.plugin.sorterContainerManager.getSorterContainers(sorterId);
		shulkerInputs = this.plugin.sorterManager.getContainers(sorterId, "shulkerinputchests");
		invalidOutputs = this.plugin.sorterManager.getContainers(sorterId, "invaliditemchests");
	}

	/**
	 * Updates the sorting process and sorts a single stack of items
	 */
	public void update() {
		// get items in the input
		ItemStack[] items = inputInventory.getStorageContents();

		// Go through the items and get the first non-invalid one
		ItemStack sortingItem = null;
		int sortingItemIndex = -1;
		for (int i = 0; i < items.length; i++) {
			if (exmptIndices.contains(i) || items[i] == null) continue;
			sortingItem = items[i];
			sortingItemIndex = i;
		}

		// No sorting index found - all items are sorted apart from invalid entries!
		if (sortingItem == null) {
			processHasCompleted = true;
			return;
		}
		
		if (shulkerInputs.size() == 0)
			this.noShulkerSortUpdate(sortingItemIndex, sortingItem);
		else
			this.shulkerSortUpdate(sortingItemIndex, sortingItem);
	}

	/**
	 * Updates the sorting system without the use of shulkers
	 * @param sortingItemIndex
	 * @param sortingItem
	 */
	private void noShulkerSortUpdate(int sortingItemIndex, ItemStack sortingItem) {

		// Find the container then sort the item
		for (SorterContainer container : this.sorterContainers) {
			if (!container.item.equals(sortingItem.getType().getKey().toString())) continue;

			Block block = container.location.getBlock();
			// This container has gone invalid for some reason - remove it
			if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) {
				this.sorterContainers.remove(container);
				this.plugin.sorterContainerManager.removeSorterContainer(container.location);
				continue;
			}

			Inventory inventory = ((Container) block.getState()).getInventory();
			// Loop through the inventory find a slot
			for (ItemStack item : inventory.getStorageContents()) {
				if (item != null) continue;
				inventory.addItem(sortingItem);
				inputInventory.removeItem(sortingItem);
				return;
			}
		}

		// Item not sorted - add it to the invalid containers
		for (DBContainer container : this.invalidOutputs) {
			Block block = container.location.getBlock();
			// This container has gone invalid for some reason - remove it
			if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) {
				this.invalidOutputs.remove(container);
				this.plugin.invalidManager.removeInvalidContainer(container.location);
				continue;
			}

			Inventory inventory = ((Container) block.getState()).getInventory();
			// Loop through the inventory find a 
			for (ItemStack item : inventory.getStorageContents()) {
				if (item != null) continue;
				inventory.addItem(sortingItem);
				inputInventory.removeItem(sortingItem);
				return;
			}
		}

		// Items not sorted AND all invalid containers chests are filled
		this.exmptIndices.add(sortingItemIndex);
	}

	/**
	 * Updates the sorting system with the use of shulkers
	 * @param sortingItemIndex
	 * @param sortingItem
	 */
	private void shulkerSortUpdate(int sortingItemIndex, ItemStack sortingItem) {

		// Find the container then sort the item
		for (SorterContainer container : this.sorterContainers) {
			if (!container.item.equals(sortingItem.getType().getKey().toString())) continue;

			Block block = container.location.getBlock();
			// This container has gone invalid for some reason - remove it
			if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) {
				this.sorterContainers.remove(container);
				this.plugin.sorterContainerManager.removeSorterContainer(container.location);
				continue;
			}

			Inventory inventory = ((Container) block.getState()).getInventory();
			// Loop through the inventory find a shulker box
			for (ItemStack item : inventory.getStorageContents()) {
				if (item == null) continue;
				if (!(item.getItemMeta() instanceof BlockStateMeta)) continue;
				BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
				if (!(meta.getBlockState() instanceof ShulkerBox)) continue;
				ShulkerBox shulker = (ShulkerBox) meta.getBlockState();
				
				// Shulker found - add item to it
				if (this.addItemToShulker(shulker, sortingItem)) {
					meta.setBlockState(shulker);
					item.setItemMeta(meta);
					inputInventory.removeItem(sortingItem);
					return;
				}
			}

			// No empty shulkers found in the inventory - get shulker from shulker input
			for (DBContainer shulkerContainer : this.shulkerInputs) {
				Block shulkerContainerblock = shulkerContainer.location.getBlock();

				// This container has gone invalid for some reason - remove it
				if (!(shulkerContainerblock.getState() instanceof Chest || shulkerContainerblock.getState() instanceof Barrel)) {
					this.shulkerInputs.remove(shulkerContainer);
					this.plugin.shulkerManager.removeShulkerInput(container.location);
					continue;
				}

				Inventory shulkerInputInventory = ((Container) shulkerContainerblock.getState()).getInventory();
				// Loop through the inventory find a shulker box
				for (ItemStack shulkerInputItem : shulkerInputInventory.getStorageContents()) {
					// Check if the item is a shulker box and get the box
					if (shulkerInputItem == null) continue;
					if (!(shulkerInputItem.getItemMeta() instanceof BlockStateMeta)) continue;
					BlockStateMeta meta = (BlockStateMeta) shulkerInputItem.getItemMeta();
					if (!(meta.getBlockState() instanceof ShulkerBox)) continue;
					ShulkerBox shulker = (ShulkerBox) meta.getBlockState();

					// Shulker found - add the item to it and end update
					if (this.addItemToShulker(shulker, sortingItem)) {
						meta.setBlockState(shulker);
						shulkerInputItem.setItemMeta(meta);
						shulkerInputInventory.removeItem(shulkerInputItem);
						inputInventory.removeItem(sortingItem);
						inventory.addItem(shulkerInputItem);
						return;
					}
				}
			}
		}

		// Item not sorted - add it to the invalid containers
		for (DBContainer container : this.invalidOutputs) {
			Block block = container.location.getBlock();
			// This container has gone invalid for some reason - remove it
			if (!(block.getState() instanceof Chest || block.getState() instanceof Barrel)) {
				this.invalidOutputs.remove(container);
				this.plugin.invalidManager.removeInvalidContainer(container.location);
				continue;
			}

			Inventory inventory = ((Container) block.getState()).getInventory();
			// Loop through the inventory find a 
			for (ItemStack item : inventory.getStorageContents()) {
				if (item != null) continue;
				inventory.addItem(sortingItem);
				inputInventory.removeItem(sortingItem);
				return;
			}
		}

		// Items not sorted AND all invalid containers chests are filled
		this.exmptIndices.add(sortingItemIndex);
	}

	/**
	 * Adds an item to a shulker box
	 * @param shulker
	 * @param item
	 * @return Whether or not the item was added
	 */
	private boolean addItemToShulker(ShulkerBox shulker, ItemStack item) {
		Inventory inventory = shulker.getSnapshotInventory();
		for (ItemStack shulkerItem : inventory.getStorageContents()) {
			if (shulkerItem != null) continue;
			inventory.addItem(item);
			return shulker.update(true, true);
		}
		
		return false;
	}

	/**
	 * Returns whether or not the process has been completed
	 * @return Whether or not the process has been completed
	 */
	public boolean completed() {
		return this.processHasCompleted;
	}
}