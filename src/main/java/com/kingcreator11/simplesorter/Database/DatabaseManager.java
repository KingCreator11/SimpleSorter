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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;

import org.bukkit.Bukkit;

/**
 * Main database manager class
 */
public class DatabaseManager extends SimpleSorterBase {

	/**
	 * The database connection
	 */
	private Connection db;

	/**
	 * Creates a new database manager instance
	 * 
	 * @param plugin
	 */
	public DatabaseManager(SimpleSorter plugin) {
		super(plugin);
	}

	/**
	 * Creates a new database if it doesn't already exist and connects to oit
	 */
	private void getDbConnection() {

		// Create database directory
		File file = new File(this.plugin.getDataFolder(), "\\");
		file.mkdir();

		// Try to get database connection
		String url = "jdbc:sqlite:"+this.plugin.getDataFolder().getAbsolutePath()+"\\database.sqlite3";

		try {
			db = DriverManager.getConnection(url);
			if (db == null) {
				plugin.getLogger().severe("Unable to connect to database due to an error");
				Bukkit.getPluginManager().disablePlugin(plugin);
				return;
			}
		}
		catch (SQLException e) {
			plugin.getLogger().severe("Unable to create database due to an error:");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
	}

	/**
	 * Creates and formats the database tables if they don't already exist
	 */
	private void createTables() {
		String[] createTablesSql = {
			"CREATE TABLE IF NOT EXISTS `inputcontainers` (" +
			"  `id` integer PRIMARY KEY," +
			"  `x` integer NOT NULL," +
			"  `y` integer NOT NULL," +
			"  `z` integer NOT NULL," +
			"  `sorterId` integer NOT NULL" +
			");",

			"CREATE TABLE IF NOT EXISTS `invaliditemchests` (" +
			"  `id` integer PRIMARY KEY," +
			"  `x` integer NOT NULL," +
			"  `y` integer NOT NULL," +
			"  `z` integer NOT NULL," +
			"  `sorterId` integer NOT NULL" +
			");",

			"CREATE TABLE IF NOT EXISTS `shulkerinputchests` (" +
			"  `id` integer PRIMARY KEY," +
			"  `x` integer NOT NULL," +
			"  `y` integer NOT NULL," +
			"  `z` integer NOT NULL," +
			"  `sorterId` integer NOT NULL" +
			");",

			"CREATE TABLE IF NOT EXISTS `sorterchest` (" +
			"  `id` integer PRIMARY KEY," +
			"  `x` integer NOT NULL," +
			"  `y` integer NOT NULL," +
			"  `z` integer NOT NULL," +
			"  `sorterId` integer NOT NULL," +
			"  `item` varchar(300) NOT NULL" +
			");",
			
			"CREATE TABLE IF NOT EXISTS `sorters` (" +
			"  `id` integer PRIMARY KEY," +
			"  `playerUUID` varchar(36) NOT NULL," +
			"  `name` varchar(30) NOT NULL" +
			");"
		};
		
		try {
			for (int i = 0; i < createTablesSql.length; i++) {
				Statement statement = db.createStatement();
				statement.execute(createTablesSql[i]);
			}
		}
		catch (SQLException e) {
			plugin.getLogger().severe("Unable to create database tables due to an error:");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
	}

	/**
	 * Loads the database
	 */
	public void loadDb() {
		getDbConnection();
		createTables();
	}
}