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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * The database manager for the input container storage
 */
public class InputContainerManager extends SimpleSorterBase {

	/**
	 * Maps online player uuids to a list of all the locations of the input containers in their sorters
	 */
	public Map<String, List<DBContainer>> inputLocations;

	/**
	 * Creates a new input manager
	 * @param plugin
	 */
	public InputContainerManager(SimpleSorter plugin) {
		super(plugin);
	}

	/**
	 * Adds an input container to the sorter
	 * @param location
	 * @param sorter
	 * @param playerUUID
	 * @return Whether or not the container was successfully added
	 */
	public String addInput(Location location, String sorter, String playerUUID) {
		if (this.plugin.sorterManager.doesContainerExist(location))
			return "A sorter is already using a container at that location";

		try {
			String sql = "INSERT INTO `inputcontainers` (x, y, z, world, sorterId) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, (int) location.getX());
			statement.setInt(2, (int) location.getY());
			statement.setInt(3, (int) location.getZ());
			statement.setString(4, location.getWorld().getName());
			statement.setInt(5, this.plugin.sorterManager.getSorter(playerUUID, sorter).id);

			if (statement.executeUpdate() != 1)
				return "Unable to add input due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to add input due to an errorr";
		}
	}

	/**
	 * Removes an input container from the sorter
	 * @param location
	 * @param playerUUID
	 * @return Whether or not the container was successfully removed
	 */
	public String removeInput(Location location, String playerUUID) {
		DBContainer container = getContainer(location);
		if (container == null)
			return "No container found at the location";
		SorterManager.Sorter sorter = this.plugin.sorterManager.getSorter(container.sorterId);
		if (!sorter.playerUUID.equals(playerUUID))
			return "The container at the location does not belongs to someone else's sorter";
		
		try {
			String sql = "DELETE FROM `inputcontainers` WHERE id = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, container.id);

			if (statement.executeUpdate() != 1)
				return "Unable to remove input due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to remove input due to an errorr";
		}
	}

	/**
	 * Gets the container
	 * @param location
	 * @return null if not found
	 */
	public DBContainer getContainer(Location location) {
		try {
			String sql = "SELECT * FROM `inputcontainers` WHERE x = ? AND y = ? AND z = ? AND world = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, (int) location.getX());
			statement.setInt(2, (int) location.getY());
			statement.setInt(3, (int) location.getZ());
			statement.setString(4, location.getWorld().getName());

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				DBContainer container = new DBContainer();
				container.id = result.getInt("id");
				container.location = new Location(
					Bukkit.getWorld(result.getString("world")),
					result.getInt("x"),
					result.getInt("y"),
					result.getInt("z")
				);
				container.sorterId = result.getInt("sorterId");
				return container;
			}
			else return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Loads the input containers for a player
	 * @param playerUUID
	 * @return Whether or not the input containers were loaded
	 */
	public void loadInputs(String playerUUID) {
		List<String> sorters = this.plugin.sorterManager.listSorters(playerUUID);
		List<DBContainer> inputContainers = new ArrayList<>();
		for (String name : sorters) {
			SorterManager.Sorter sorter = this.plugin.sorterManager.getSorter(playerUUID, name);

			try {
				String sql = "SELECT * FROM `inputcontainers` WHERE sorterId = ?";
				PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
				statement.setInt(1, sorter.id);
	
				ResultSet result = statement.executeQuery();
				if (!result.next()) continue;
				
				DBContainer container = new DBContainer();
				container.id = result.getInt("id");
				container.location = new Location(
					Bukkit.getWorld(result.getString("world")),
					result.getInt("x"),
					result.getInt("y"),
					result.getInt("z")
				);
				container.sorterId = result.getInt("sorterId");
				inputContainers.add(container);
			}
			catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}

		this.inputLocations.put(playerUUID, inputContainers);
	}

	/**
	 * Unloads the input containers for a player
	 * @param playerUUID
	 */
	public void unloadInputs(String playerUUID) {
		this.inputLocations.remove(playerUUID);
	}
}