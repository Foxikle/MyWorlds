package com.bergerkiller.bukkit.mw.commands;

import org.bukkit.ChatColor;

import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.WorldConfig;

public class WorldFormSnow extends Command {

	public WorldFormSnow() {
		super(Permission.COMMAND_FORMING, "world.formsnow");
	}

	public void execute() {
		if (args.length != 0) {
			this.genWorldname(1);
			if (this.handleWorld()) {
				WorldConfig wc = WorldConfig.get(worldname);
				if (ParseUtil.isBool(args[0])) {
					wc.formSnow = ParseUtil.parseBool(args[0]);
					if (wc.formSnow) {
						message(ChatColor.YELLOW + "Snow will now form on world '" + worldname + "'!");
					} else {
						message(ChatColor.YELLOW + "Snow will no longer form on world '" + worldname + "'!");
					}
				} else {
					if (wc.formSnow) {
						message(ChatColor.YELLOW + "Snow can form on world '" + worldname + "'!");
					} else {
						message(ChatColor.YELLOW + "Snow can not form on world '" + worldname + "'!");
					}
				}
			}
		} else {
			showInv();
		}
	}
}
