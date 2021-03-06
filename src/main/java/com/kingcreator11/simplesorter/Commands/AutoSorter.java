package com.kingcreator11.simplesorter.Commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.kingcreator11.simplesorter.SimpleSorter;
import com.kingcreator11.simplesorter.SimpleSorterBase;
import com.kingcreator11.simplesorter.Database.DBContainer;
import com.kingcreator11.simplesorter.SortingProcess.SortingProcess;

import org.bukkit.block.Block;
import org.bukkit.block.Barrel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;



public class AutoSorter extends SubCommand {
    
	/**
	 * Creates a new AutoSorter command instance
	 * @param plugin
	 */
	public AutoSorter(SimpleSorter plugin) {
		super(plugin, new String[] {"simplesorter.usage"}, SubCommandType.argsList);
	}

	/*
	ripped from listenhandler
	*/
	private Map<DBContainer, SortingProcessRunner> onGoingSorters = new HashMap<>();
	private class SortingProcessRunner extends BukkitRunnable {

		/**
		 * The process used for sorting
		 */
		private SortingProcess process;

		/**
		 * The input container this process is mapped to
		 */
		DBContainer input;

		/*
			we need these if the player deletes the sorter we check to make sure
			it still exists.
		*/
		String sorter;
		String playerUUID;
		SimpleSorter plugin;

		/**
		 * Creates a new sorting process runner
		 * @param plugin
		 * @param input
		 */
		public SortingProcessRunner(SimpleSorter plugin, DBContainer input, Inventory inputInv, String sorter, String playerUUID) {
			process = new SortingProcess(plugin, inputInv, input.sorterId);
			this.input = input;
			this.sorter = sorter;
			this.playerUUID = playerUUID;
			this.plugin = plugin;
		}

		/**
		 * Runs and updates the sorting process
		 */
		@Override
		public void run() {
			// when we turn off autosort we remove the key from onGoingSorters,
			// so check for the key and if not there close the process.
			if (onGoingSorters.containsKey(this.input) && this.plugin.sorterManager.getSorter(this.playerUUID, this.sorter) != null) 
				process.update();
			else {
				onGoingSorters.remove(input);
				this.cancel();
			}
			/*
			Since we're autosorting if the process is complete, we do nothing!
			if (process.completed()) {
				// Process completed! remove ourselves from the map
				onGoingSorters.remove(input);
				this.cancel();
			}
			*/
		}
	}



	/**
	 * Executes the sub command
	 * @param sender The sender of the command
	 * @param args The arguments of the command
	 */
	@Override
	protected void executeCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("??cOnly players may use this command");
			return;
		}

		Player player = (Player) sender;
		String playerUUID = player.getUniqueId().toString();
		RayTraceResult result = player.rayTraceBlocks(10);
		boolean isDoubleChest = false;

		if (result == null) {
			sender.sendMessage("??cPlease point at a container to sort");
			return;
		}

		Block block = result.getHitBlock();


		/* these ifs just make sure the player ran the command properly */
		if (block == null || !this.blockIsSorterContainer(block)) {
			sender.sendMessage("??cPlease point at a container to sort");
			return;
		}
		Container ch = (Container) block.getState(); // make block a chest
		if (args.length < 2) {
			sender.sendMessage("??cCommand usage /ss autosort <name> <seconds>");
			return;
		}
		if (args[0].isEmpty() || args[1].isEmpty()) {
			sender.sendMessage("??cCommand usage /ss autosort <name> <seconds>");
			return;
		}
		if (!args[1].matches("^[0-9]+$")) {
			sender.sendMessage("??cSeconds must be a valid number.");
			return;
		}
		if (ch.getInventory() instanceof DoubleChestInventory) 
			isDoubleChest = true;

		/* 
			ripped from listener, we find the container we're looking for.
		*/
		// Loop through containers and check if this is one of them
		List<DBContainer> containers = this.plugin.inputManager.inputLocations.get(playerUUID);
		DBContainer input = null;

		// Not a double chest - normal looping through is fine
		if (!isDoubleChest) {
			for (DBContainer container : containers){
				if (container.location.equals(ch.getLocation())) {
					input = container;
					break;
				}
			}
		}
		// Double chests need checking for both sides
		else {
			DoubleChest doubleChest = (DoubleChest) ch.getInventory().getHolder();
			Chest left = (Chest) doubleChest.getLeftSide();
			Chest right = (Chest) doubleChest.getRightSide();
			for (DBContainer container : containers){
				if (container.location.equals(left.getLocation()) || container.location.equals(right.getLocation())) {
					input = container;
					break;
				}
			}
		}



		// The container in question isn't an input container
		if (input == null) {
			player.sendMessage("not an input container");	
			return;
		}


		// Check if there's already a sorting process occurring for this chest
		// if so we turn off the autosorting.
		if (this.onGoingSorters.containsKey(input)) {
			player.sendMessage("turning off autosort for this input chest");
			onGoingSorters.remove(input);
			return;
		}


		// All checks done - this chest is an input chest.
		player.sendMessage("Auto sorting process will begin in 5 seconds and move one stack every " + args[1] + " seconds.");

		// Create sorting process runner and run it
		SortingProcessRunner runner = new SortingProcessRunner(this.plugin, input, ch.getInventory(), args[0], playerUUID);
		this.onGoingSorters.put(input, runner);

		// Ideally 5 seconds after but can be delayed by lag
		// our interval is also the desired interval by the user in seconds.
		int interval = Integer.parseInt(args[1])*20;
		runner.runTaskTimer(this.plugin, 5*20, interval);
	}

	// player leaves we turn off all auto sorting by removing all the keys from onGoingSorters
	// which will kill all the processes.
	public void stop(String playerUUID) {
		List<DBContainer> containers = this.plugin.inputManager.inputLocations.get(playerUUID);
		for (DBContainer c : containers) {
			if (this.onGoingSorters.containsKey(c))
				this.onGoingSorters.remove(c);
		}
	}
}
