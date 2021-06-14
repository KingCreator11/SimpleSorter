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
	 * Called when the plugin is enabled - startup process
	 */
	@Override
	public void onEnable() {
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
		this.commandHandler.addSubCommand("sortname", new SortNameCommand(this));
		this.commandHandler.addSubCommand("removesorter", new RemoveSorterCommand(this));
	}

	/**
	 * Called before the plugin is disabled - ending process
	 */
	@Override
	public void onDisable() {
		
	}
}