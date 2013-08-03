package com.bergerkiller.bukkit.mw.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.utils.TimeUtil;
import com.bergerkiller.bukkit.mw.Localization;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.TimeControl;
import com.bergerkiller.bukkit.mw.WorldConfig;
import com.bergerkiller.bukkit.mw.WorldInfo;
import com.bergerkiller.bukkit.mw.WorldManager;

public class WorldTime extends Command {

	public WorldTime() {
		super(Permission.COMMAND_TIME, "world.time");
	}

	public void execute() {
		boolean lock = false;
		boolean useWorld = false;
		long time = -1;
		for (String command : args) {
			//Time reading
			if (command.equalsIgnoreCase("lock")) {
				lock = true;
			} else if (command.equalsIgnoreCase("locked")) {
				lock = true;
			} else if (command.equalsIgnoreCase("always")) {
				lock = true;
			} else if (command.equalsIgnoreCase("endless")) {
				lock = true;
			} else if (command.equalsIgnoreCase("l")) {
				lock = true;
			} else if (command.equalsIgnoreCase("-l")) {
				lock = true;	
			} else if (command.equalsIgnoreCase("stop")) {
				lock = true;
			} else if (command.equalsIgnoreCase("freeze")) {
				lock = true;
			} else {
				long newtime = TimeUtil.getTime(command);
				if (newtime != -1) {
					time = newtime;
				} else {
					//Used the last argument as command?
					if (command == args[args.length - 1]) useWorld = true;
				}
			}
		}
		worldname = WorldManager.getWorldName(sender, args, useWorld);
		if (this.handleWorld()) {
			if (time == -1) {
				World w = WorldManager.getWorld(worldname);
				if (w == null) {
					WorldInfo i = WorldManager.getInfo(worldname);
					if (i == null) {
						time = 0;
					} else {
						time = i.time;
					}
				} else {
					time = w.getFullTime();
				}
			}
			if (args.length == 0) {
				message(ChatColor.YELLOW + "The current time of world '" + 
						worldname + "' is " + TimeUtil.getTimeString(time));
			} else {
				TimeControl tc = WorldConfig.get(worldname).timeControl;
				boolean wasLocked = tc.isLocked();
				tc.setLocking(lock);
				tc.setTime(time);
				if (lock) {
					if (WorldManager.isLoaded(worldname)) {
						message(ChatColor.GREEN + "Time of world '" + worldname + "' locked to " + 
								TimeUtil.getTimeString(time) + "!");
					} else {
						Localization.WORLD_NOTLOADED.message(sender, worldname);
						message(ChatColor.YELLOW + "Time will be locked to " + 
								TimeUtil.getTimeString(time) + " as soon it is loaded!");
					}
				} else {
					World w = WorldManager.getWorld(worldname);
					if (w != null) {
						if (wasLocked) {
							message(ChatColor.GREEN + "Time of world '" + worldname + "' unlocked and set to " + 
									TimeUtil.getTimeString(time) + "!");
						} else {
							message(ChatColor.GREEN + "Time of world '" + worldname + "' set to " + 
									TimeUtil.getTimeString(time) + "!");
						}
					} else {
						Localization.WORLD_NOTLOADED.message(sender, worldname);
						message(ChatColor.YELLOW + "Time has not been changed!");
					}
				}
			}
		}
	}
}
