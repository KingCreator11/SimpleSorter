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

import org.bukkit.Location;

/**
 * The database manager for the main sorter storage
 */
public class SorterManager extends SimpleSorterBase {

	/**
	 * Represents a single sorter entry
	 */
	public class Sorter {

		/**
		 * The name of the sorter
		 */
		public String name;

		/**
		 * The player who owns the sorter
		 */
		public String playerUUID;

		/**
		 * The id of the sorter
		 */
		public int id;
	}

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
	 * @return Whether or not the sorter was created/a description of why
	 */
	public String createSorter(String name, String playerUUID) {
		try {
			String sql = "INSERT INTO `sorters` (name, playerUUID) VALUES (?, ?)";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setString(1, name);
			statement.setString(2, playerUUID);
			if (statement.executeUpdate() != 1)
				return "Unable to create sorter due to an error";
			else
				return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to create sorter due to an error";
		}
	}

	/**
	 * Deletes a sorter
	 * @param name The name of the sorter to delete
	 * @param playerUUID The player id to delete it from
	 * @return Whether or not the sorter was deleted/a description of why
	 */
	public String deleteSorter(String name, String playerUUID) {
		Sorter sorter = getSorter(playerUUID, name);
		try {
			String sql = "DELETE FROM `sorters` WHERE id = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, sorter.id);
			if (statement.executeUpdate() != 1)
				return "Sorter not found";
			else {
				deleteSorter(sorter.id);
				return null;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return "Unable to delete sorter due to an error";
		}
	}

	/**
	 * Delets all associated to a sorter from all other tables
	 * @param sorterId
	 */
	private void deleteSorter(int sorterId) {
		String[] tables = {"inputcontainers", "invaliditemchests", "shulkerinputchests", "sorterchest"};

		for (String table : tables) {
			try {
				String sql = "DELETE FROM `"+table+"` WHERE sorterId = ?";
				PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
				statement.setInt(1, sorterId);
				statement.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks if a container exists across all tables
	 * @param location
	 * @return Whether or not the container exists
	 */
	public boolean doesContainerExist(Location location) {
		String[] tables = {"inputcontainers", "invaliditemchests", "shulkerinputchests", "sorterchest"};
		
		for (String table : tables) {
			try {
				String sql = "SELECT * FROM `" + table + "` WHERE x = ? AND y = ? AND z = ? AND world = ?";
				PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
				statement.setInt(1, (int) location.getX());
				statement.setInt(2, (int) location.getY());
				statement.setInt(3, (int) location.getZ());
				statement.setString(4, location.getWorld().getName());
				
				ResultSet result = statement.executeQuery();
				if (result.next())
					return true;
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	public void removeAtCoords(Location location) {
		String[] tables = {"inputcontainers", "invaliditemchests", "shulkerinputchests", "sorterchest"};
		
		for (String table : tables) {
			try {
				String sql = "DELETE FROM `" + table + "` WHERE x = ? AND y = ? AND z = ? AND world = ?";
				PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
				statement.setInt(1, (int) location.getX());
				statement.setInt(2, (int) location.getY());
				statement.setInt(3, (int) location.getZ());
				statement.setString(4, location.getWorld().getName());
				statement.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Lists the sorters names for a player
	 * @param playerUUID The player to list for
	 * @return The list of the sorter names
	 */
	public List<String> listSorters(String playerUUID) {
		try {
			String sql = "SELECT name FROM `sorters` WHERE playerUUID = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setString(1, playerUUID);
			ResultSet results = statement.executeQuery();
			List<String> list = new ArrayList<>();

			while (results.next()) {
				list.add(results.getString("name"));
			}

			return list;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a sorter
	 * @param playerUUID
	 * @param name
	 * @return The sorter, null if not found
	 */
	public Sorter getSorter(String playerUUID, String name) {
		try {
			String sql = "SELECT * FROM `sorters` WHERE playerUUID = ? AND name = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setString(1, playerUUID);
			statement.setString(2, name);
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				Sorter sorter = new Sorter();
				sorter.id = results.getInt("id");
				sorter.name = results.getString("name");
				sorter.playerUUID = results.getString("playerUUID");
				return sorter;
			}
			else return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets a sorter
	 * @param sorterId
	 * @return null if not ofund
	 */
	public Sorter getSorter(int sorterId) {
		try {
			String sql = "SELECT * FROM `sorters` WHERE id = ?";
			PreparedStatement statement = this.plugin.dbManager.db.prepareStatement(sql);
			statement.setInt(1, sorterId);
			ResultSet results = statement.executeQuery();
			
			if (results.next()) {
				Sorter sorter = new Sorter();
				sorter.id = results.getInt("id");
				sorter.name = results.getString("name");
				sorter.playerUUID = results.getString("playerUUID");
				return sorter;
			}
			else return null;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}