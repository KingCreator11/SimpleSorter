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

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * The database manager for the shulker input
 */
public class ShulkerInputManager extends SimpleSorterBase {

	/**
	 * Creates a new shulker input manager
	 * @param plugin
	 */
	public ShulkerInputManager(SimpleSorter plugin) {
		super(plugin);
	}
	
	/**
	 * Adds a shulker input container to the storage
	 * @param location
	 * @param sorter
	 * @param playerUUID
	 * @return Whether or not the input was added
	 */
	public String addShulkerInput(Location location, String sorter, String playerUUID) {
		if (this.plugin.sorterManager.doesContainerExist(location))
			return "A sorter is already using a container at that location";

		try {
			String sql = "INSERT INTO `shulkerinputchests` (x, y, z, world, sorterId) VALUES (?, ?, ?, ?, ?)";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, (int) location.getX());
			statement.setInt(2, (int) location.getY());
			statement.setInt(3, (int) location.getZ());
			statement.setString(4, location.getWorld().getName());
			SorterManager.Sorter sorterObj = this.plugin.sorterManager.getSorter(playerUUID, sorter);
			if (sorterObj == null) return "Sorter with that name not found.";
			statement.setInt(5, sorterObj.id);

			if (statement.executeUpdate() != 1)
				return "Unable to add container due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			this.plugin.getLogger().warning("Unable to add shulker input at " + location + " for sorter " + sorter
				+ " for player " + playerUUID);
			e.printStackTrace();
			return "Unable to add container due to an errorr";
		}
	}

	/**
	 * Removes a shulker input container from the storage
	 * @param location
	 * @param playerUUID
	 * @return Whether or not the container was removed
	 */
	public String removeShulkerInput(Location location, String playerUUID) {
		DBContainer container = getContainer(location);
		if (container == null)
			return "No container found at the location";
		SorterManager.Sorter sorter = this.plugin.sorterManager.getSorter(container.sorterId);
		if (!sorter.playerUUID.equals(playerUUID))
			return "The container at the location does not belongs to someone else's sorter";
		
		try {
			String sql = "DELETE FROM `shulkerinputchests` WHERE id = ?";
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
	 * Removes a shulker input container from the storage
	 * @param location
	 * @return Whether or not the container was removed
	 */
	public String removeShulkerInput(Location location) {
		DBContainer container = getContainer(location);
		if (container == null)
			return "No container found at the location";
		
		try {
			String sql = "DELETE FROM `shulkerinputchests` WHERE id = ?";
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
			String sql = "SELECT * FROM `shulkerinputchests` WHERE x = ? AND y = ? AND z = ? AND world = ?";
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
}