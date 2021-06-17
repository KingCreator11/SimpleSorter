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

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * The database manager for the main sorter container storage
 */
public class SorterContainerManager extends SimpleSorterBase {

	/**
	 * Represents a single db sorter container
	 */
	public class SorterContainer {
		public int id;
		public Location location;
		public int sorterId;
		public String item;
	}

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
	public String addSorterContainer(Location location, String sorter, String playerUUID, String itemId) {
		if (this.plugin.sorterManager.doesContainerExist(location))
			return "A sorter is already using a container at that location";

		try {
			String sql = "INSERT INTO `sorterchest` (x, y, z, world, sorterId, item) VALUES (?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, (int) location.getX());
			statement.setInt(2, (int) location.getY());
			statement.setInt(3, (int) location.getZ());
			statement.setString(4, location.getWorld().getName());
			SorterManager.Sorter sorterObj = this.plugin.sorterManager.getSorter(playerUUID, sorter);
			if (sorterObj == null) return "Unable to find a sorter with that name";
			statement.setInt(5, sorterObj.id);
			statement.setString(6, itemId);

			if (statement.executeUpdate() != 1)
				return "Unable to add sorter due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to add input due to an errorr";
		}
	}

	/**
	 * Removes a sorter from the system
	 * @param location
	 * @param playerUUID
	 * @return Whether or not the sorter was removed
	 */
	public String removeSorterContainer(Location location, String playerUUID) {
		SorterContainer container = getSorterContainer(location);
		if (container == null)
			return "No container found at the location";
		SorterManager.Sorter sorter = this.plugin.sorterManager.getSorter(container.sorterId);
		if (!sorter.playerUUID.equals(playerUUID))
			return "The container at the location does not belongs to someone else's sorter";
		
		try {
			String sql = "DELETE FROM `sorterchest` WHERE id = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, container.id);

			if (statement.executeUpdate() != 1)
				return "Unable to remove sorter due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to remove sorter due to an errorr";
		}
	}

	/**
	 * Removes a sorter from the system
	 * @param location
	 * @return Whether or not the sorter was removed
	 */
	public String removeSorterContainer(Location location) {
		SorterContainer container = getSorterContainer(location);
		if (container == null)
			return "No container found at the location";
		
		try {
			String sql = "DELETE FROM `sorterchest` WHERE id = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, container.id);

			if (statement.executeUpdate() != 1)
				return "Unable to remove sorter due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to remove sorter due to an errorr";
		}
	}

	/**
	 * Gets a sorter container given a location
	 * @param location
	 * @return null if not found
	 */
	public SorterContainer getSorterContainer(Location location) {
		try {
			String sql = "SELECT * FROM `sorterchest` WHERE x = ? AND y = ? AND z = ? AND world = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, (int) location.getX());
			statement.setInt(2, (int) location.getY());
			statement.setInt(3, (int) location.getZ());
			statement.setString(4, location.getWorld().getName());

			ResultSet result = statement.executeQuery();
			if (result.next()) {
				SorterContainer container = new SorterContainer();
				container.id = result.getInt("id");
				container.location = new Location(
					Bukkit.getWorld(result.getString("world")),
					result.getInt("x"),
					result.getInt("y"),
					result.getInt("z")
				);
				container.sorterId = result.getInt("sorterId");
				container.item = result.getString("item");
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
	 * Gets all sorter containers for a sorter
	 * @param sorterId
	 * @return
	 */
	public List<SorterContainer> getSorterContainers(int sorterId) {
		List<SorterContainer> list = new ArrayList<>();

		try {
			String sql = "SELECT * FROM `sorterchest` WHERE sorterId = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, sorterId);

			ResultSet result = statement.executeQuery();
			while (result.next()) {
				SorterContainer container = new SorterContainer();
				container.id = result.getInt("id");
				container.location = new Location(
					Bukkit.getWorld(result.getString("world")),
					result.getInt("x"),
					result.getInt("y"),
					result.getInt("z")
				);
				container.sorterId = result.getInt("sorterId");
				container.item = result.getString("item");
				list.add(container);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return list;
	}
}