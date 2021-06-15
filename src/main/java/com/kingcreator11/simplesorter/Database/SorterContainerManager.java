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

package com.kingcreator11.simplesorter.Database;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.Location;

/**
 * The database manager for the main sorter container storage
 */
public class SorterContainerManager extends SimpleSorterBase {

	/**
	 * Creates a new sorter container manager
	 * @param plugin
	 */
	public SorterContainerManager(SimpleSorter plugin) {
		super(plugin);
	}
	
	/**
	 * Adds a container to the sorter to sort out a particular item
	 * @param location
	 * @param sorter
	 * @param playerUUID
	 * @param itemId
	 * @return Whether or not the sorter was added
	 */
	public boolean addSorterContainer(Location location, String sorter, String playerUUID, String itemId) {
		return false;
	}

	/**
	 * Removes a sorter from the system
	 * @param location
	 * @param playerUUID
	 * @return Whether or not the sorter was removed
	 */
	public boolean removeSorterContainer(Location location, String playerUUID) {
		return false;
	}
}