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
 * The database manager for the invalid container storage
 */
public class InvalidContainerManager extends SimpleSorterBase {

	/**
	 * Creates a new invalid container manager
	 * @param plugin
	 */
	public InvalidContainerManager(SimpleSorter plugin) {
		super(plugin);
	}
	
	/**
	 * Adds an invalid item container to the sorter
	 * @param location
	 * @param sorter
	 * @param playerUUID
	 * @return Whether or not the container was added
	 */
	public boolean addInvalidContainer(Location location, String sorter, String playerUUID) {
		return false;
	}

	/**
	 * Removes an invalid container from the sorter
	 * @param location
	 * @param playerUUID
	 * @return Whether or not the container was removed
	 */
	public boolean removeInvalidContainer(Location location, String playerUUID) {
		return false;
	}
}