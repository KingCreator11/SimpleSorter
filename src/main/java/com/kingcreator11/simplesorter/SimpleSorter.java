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

package com.kingcreator11.simplesorter;

import com.kingcreator11.simplesorter.Commands.*;
import com.kingcreator11.simplesorter.Database.*;
import com.kingcreator11.simplesorter.Listeners.ListenerHandler;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main simple sorter plugin class
 */
public class SimpleSorter extends JavaPlugin {

	/**
	 * The command handler used for this plugin instance
	 */
	private CommandHandler commandHandler = new CommandHandler(this);

	/**
	 * The main events handler
	 */
	public ListenerHandler eventsHandler = new ListenerHandler(this);

	/**
	 * The main database manager
	 */
	public DatabaseManager dbManager = new DatabaseManager(this);
	
	/**
	 * The database manager instance for InputContainers 
	 */
	public InputContainerManager inputManager = new InputContainerManager(this);
	
	/**
	 * The database manager instance for InvalidContainers 
	 */
	public InvalidContainerManager invalidManager = new InvalidContainerManager(this);
	
	/**
	 * The database manager instance for ShulkerInputs 
	 */
	public ShulkerInputManager shulkerManager = new ShulkerInputManager(this);
	
	/**
	 * The database manager instance for SorterContainers 
	 */
	public SorterContainerManager sorterContainerManager = new SorterContainerManager(this);
	
	/**
	 * The database manager instance for Sorters 
	 */
	public SorterManager sorterManager = new SorterManager(this);


	/**
	 * Auto sorting functionality.
	 */
	public AutoSorter autoManager = new AutoSorter(this);
	

	/**
	 * Called when the plugin is enabled - startup process
	 */
	@Override
	public void onEnable() {

		// Setup database
		dbManager.loadDb();

		// Init commands
		this.getCommand("ss").setExecutor(commandHandler);
		this.getCommand("simplesorter").setExecutor(commandHandler);

		// Init sub commands
		this.commandHandler.addSubCommand("help", new HelpCommand(this));
		this.commandHandler.addSubCommand("create", new CreateCommand(this));
		this.commandHandler.addSubCommand("delete", new DeleteCommand(this));
		this.commandHandler.addSubCommand("list", new ListCommand(this));
		this.commandHandler.addSubCommand("setinput", new SetInputCommand(this));
		this.commandHandler.addSubCommand("removeinput", new RemoveInputCommand(this));
		this.commandHandler.addSubCommand("setinvalid", new SetInvalidCommand(this));
		this.commandHandler.addSubCommand("removeinvalid", new RemoveInvalidCommand(this));
		this.commandHandler.addSubCommand("setshulkerinput", new SetShulkerInputCommand(this));
		this.commandHandler.addSubCommand("removeshulkerinput", new RemoveShulkerInputCommand(this));
		this.commandHandler.addSubCommand("sort", new SortCommand(this));
		this.commandHandler.addSubCommand("removesorter", new RemoveSorterCommand(this));
		this.commandHandler.addSubCommand("autosort", autoManager);

		// Events
		getServer().getPluginManager().registerEvents(eventsHandler, this);
	}

	/**
	 * Called before the plugin is disabled - ending process
	 */
	@Override
	public void onDisable() {
		
	}
}