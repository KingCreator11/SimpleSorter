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

import java.util.List;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

/**
 * The database manager for the main sorter storage
 */
public class SorterManager extends SimpleSorterBase {

	/**
	 * Creates a new sorter manager
	 * @param plugin
	 */
	public SorterManager(SimpleSorter plugin) {
		super(plugin);
	}
	
	/**
	 * Creates a new sorter
	 * @param name The name of the sorter
	 * @param playerUUID The player UUID of the creator
	 * @return Whether or not the sorter was created
	 */
	public boolean createSorter(String name, String playerUUID) {
		return false;
	}

	/**
	 * Deletes a sorter
	 * @param name The name of the sorter to delete
	 * @param playerUUID The player id to delete it from
	 * @return Whether or not the sorter was deleted
	 */
	public boolean deleteSorter(String name, String playerUUID) {
		return false;
	}

	/**
	 * Lists the sorters names for a player
	 * @param playerUUID The player to list for
	 * @return The list of the sorter names
	 */
	public List<String> listSorters(String playerUUID) {
		return null;
	}
}