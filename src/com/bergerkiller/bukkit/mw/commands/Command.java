package com.bergerkiller.bukkit.mw.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.common.MessageBuilder;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.mw.Localization;
import com.bergerkiller.bukkit.mw.MyWorlds;
import com.bergerkiller.bukkit.mw.Permission;
import com.bergerkiller.bukkit.mw.Portal;
import com.bergerkiller.bukkit.mw.Util;
import com.bergerkiller.bukkit.mw.WorldManager;

public class Command {
	public Permission permission;
	public String commandNode;
	public String command;
	public Player player;
	public CommandSender sender;
	public String[] args;
	public String worldname;

	private static String[] commandNodes = new String[] {"world.repair", "world.delete", "world.rename", "world.copy",
		"world.save", "world.load", "world.unload", "world.reloadwhenempty", "world.create", "world.listgenerators",
		"world.weather", "world.time", "world.spawn", "world.setspawn", "world.list", "world.info", "world.portals",
		"world.gamemode", "world.togglepvp", "world.op", "world.deop",  "world.allowspawn", "tpp"};

	public Command(Permission permission, String commandNode) {
		this.permission = permission;
		this.commandNode = commandNode;
	}

	public void init(CommandSender sender, String[] args) {
		this.args = args;
		this.sender = sender;
		if (sender instanceof Player) {
			this.player = (Player) sender;
		}
	}

	public static boolean allowConsole(String node) {
		if (node.equals("world.setspawn")) return false;
		if (node.equals("world.spawn")) return false;
		return true;
	}

	public void removeArg(int index) {
		String[] newargs = new String[args.length - 1];
		int ni = 0;
		for (int i = 0; i < args.length; i++) {
			if (i == index) continue;
			newargs[ni] = args[i];
			ni++;
		}
		this.args = newargs;
	}

	public boolean hasPermission() {
		return this.hasPermission(this.permission.getName());
	}

	public boolean hasPermission(String node) {
		if (this.player == null) {
			return allowConsole(node);
		} else {
			return Permission.has(this.player, node);
		}
	}

	public boolean handleWorld() {
		if (this.worldname == null) {
			locmessage(Localization.WORLD_NOTFOUND);
		}
		return this.worldname != null;
	}
	
	public void messageNoSpout() {
		if (MyWorlds.isSpoutEnabled) return;
		this.message(ChatColor.YELLOW + "Note that Spout is not installed right now!");
	}
	public void message(String msg) {
		if (msg == null) return;
		CommonUtil.sendMessage(this.sender, msg);
	}

	public void locmessage(Localization node, String... arguments) {
		node.message(this.sender, arguments);
	}

	public void notifyConsole(String message) {
		Util.notifyConsole(sender, message);
	}

	public boolean showInv() {
		return this.showInv(this.commandNode);
	}

	public boolean showInv(String node) {
		message(ChatColor.RED + "Invalid arguments for this command!");
		return showUsage(node);
	}

	public boolean showUsage() {
		return showUsage(this.commandNode);
	}

	public boolean showUsage(String commandNode) {
		if (hasPermission()) {
			this.sender.sendMessage(MyWorlds.plugin.getCommandUsage(commandNode));
			return true;
		} else {
			return false;
		}
	}

	public void listPortals(String[] portals) {
		MessageBuilder builder = new MessageBuilder();
		builder.green("[Very near] ").dark_green("[Near] ").yellow("[Far] ");
		builder.red("[Other world] ").dark_red("[Unavailable]").newLine();
		builder.yellow("Available portals: ").white(portals.length, " Portal");
		if (portals.length != 1) builder.append('s');
		if (portals.length > 0) {
			builder.newLine().setIndent(2).setSeparator(ChatColor.WHITE, " / ");
			final Location ploc;
			if (sender instanceof Player) {
				ploc = ((Player) sender).getLocation();
			} else {
				ploc = null;
			}
			for (String portal : portals) {
				Location loc = Portal.getPortalLocation(portal, null);
				if (loc != null && ploc != null) {
					if (ploc.getWorld() == loc.getWorld()) {
						double d = ploc.distance(loc);
						if (d <= 10) {
							builder.green(portal);
						} else if (d <= 100) {
							builder.dark_green(portal);
						} else {
							builder.yellow(portal);
						}
					} else {
						builder.red(portal);
					}
				} else {
					builder.dark_red(portal);
				}
			}
		}
		builder.send(sender);
	}
	
	public void genWorldname(int argindex) {
		if (argindex >= 0 && argindex < this.args.length) {
			this.worldname = WorldManager.matchWorld(args[argindex]);
			if (this.worldname != null) return;
		}
		if (player != null) {
			this.worldname = player.getWorld().getName();
		} else {
			this.worldname = Bukkit.getServer().getWorlds().get(0).getName();
		}
	}
			
	public static void execute(CommandSender sender, String cmdLabel, String[] args) {
		//generate a node from this command
		Command rval = null;
		if (cmdLabel.equalsIgnoreCase("world")
				|| cmdLabel.equalsIgnoreCase("myworlds")
				|| cmdLabel.equalsIgnoreCase("worlds")
				|| cmdLabel.equalsIgnoreCase("mw")) {
			if (args.length >= 1) {
				cmdLabel = args[0];
				args = StringUtil.remove(args, 0);
				if (cmdLabel.equalsIgnoreCase("list")) {
					rval = new WorldList();
				} else if (cmdLabel.equalsIgnoreCase("info")) {
					rval = new WorldInfo();
				} else if (cmdLabel.equalsIgnoreCase("i")) {
					rval = new WorldInfo();
				} else if (cmdLabel.equalsIgnoreCase("portals")) {
					rval = new WorldPortals();
				} else if (cmdLabel.equalsIgnoreCase("portal")) {
					rval = new WorldPortals();
				} else if (cmdLabel.equalsIgnoreCase("load")) {
					rval = new WorldLoad();
				} else if (cmdLabel.equalsIgnoreCase("unload")) {
					rval = new WorldUnload();
				} else if (cmdLabel.equalsIgnoreCase("create")) {
					rval = new WorldCreate();
				} else if (cmdLabel.equalsIgnoreCase("spawn")) {
					rval = new WorldSpawn();
				} else if (cmdLabel.equalsIgnoreCase("evacuate")) {
					rval = new WorldEvacuate();
				} else if (cmdLabel.equalsIgnoreCase("evac")) {
					rval = new WorldEvacuate();
				} else if (cmdLabel.equalsIgnoreCase("repair")) {
					rval = new WorldRepair();
				} else if (cmdLabel.equalsIgnoreCase("rep")) {
					rval = new WorldRepair();
				} else if (cmdLabel.equalsIgnoreCase("save")) {
					rval = new WorldSave();
				} else if (cmdLabel.equalsIgnoreCase("delete")) {
					rval = new WorldDelete();
				} else if (cmdLabel.equalsIgnoreCase("del")) {
					rval = new WorldDelete();
				} else if (cmdLabel.equalsIgnoreCase("copy")) {
					rval = new WorldCopy();
				} else if (cmdLabel.equalsIgnoreCase("togglepvp")) {
					rval = new WorldTogglePVP();
				} else if (cmdLabel.equalsIgnoreCase("tpvp")) {
					rval = new WorldTogglePVP();
				} else if (cmdLabel.equalsIgnoreCase("pvp")) {
					rval = new WorldTogglePVP();		
				} else if (cmdLabel.equalsIgnoreCase("weather")) {
					rval = new WorldWeather();
				} else if (cmdLabel.equalsIgnoreCase("w")) {
					rval = new WorldWeather();
				} else if (cmdLabel.equalsIgnoreCase("time")) {
					rval = new WorldTime();
				} else if (cmdLabel.equalsIgnoreCase("t")) {
					rval = new WorldTime();
				} else if (cmdLabel.equalsIgnoreCase("allowspawn")) {
					rval = new WorldSpawning(true);
				} else if (cmdLabel.equalsIgnoreCase("denyspawn")) {
					rval = new WorldSpawning(false);
				} else if (cmdLabel.equalsIgnoreCase("spawnallow")) {
					rval = new WorldSpawning(true);
				} else if (cmdLabel.equalsIgnoreCase("spawndeny")) {
					rval = new WorldSpawning(false);
				} else if (cmdLabel.equalsIgnoreCase("allowspawning")) {
					rval = new WorldSpawning(true);
				} else if (cmdLabel.equalsIgnoreCase("denyspawning")) {
					rval = new WorldSpawning(false);
				} else if (cmdLabel.equalsIgnoreCase("setportal")) {
					rval = new WorldSetPortal();
				} else if (cmdLabel.equalsIgnoreCase("setdefaultportal")) {
					rval = new WorldSetPortal();
				} else if (cmdLabel.equalsIgnoreCase("setdefportal")) {
					rval = new WorldSetPortal();
				} else if (cmdLabel.equalsIgnoreCase("setdefport")) {
					rval = new WorldSetPortal();
				} else if (cmdLabel.equalsIgnoreCase("setspawn")) {
					rval = new WorldSetSpawn();
				} else if (cmdLabel.equalsIgnoreCase("gamemode")) {
					rval = new WorldGamemode();
				} else if (cmdLabel.equalsIgnoreCase("setgamemode")) {
					rval = new WorldGamemode();
				} else if (cmdLabel.equalsIgnoreCase("gm")) {
					rval = new WorldGamemode();
				} else if (cmdLabel.equalsIgnoreCase("setgm")) {
					rval = new WorldGamemode();
				} else if (cmdLabel.equalsIgnoreCase("generators")) {
					rval = new WorldListGenerators();
				} else if (cmdLabel.equalsIgnoreCase("gen")) {
					rval = new WorldListGenerators();
				} else if (cmdLabel.equalsIgnoreCase("listgenerators")) {
					rval = new WorldListGenerators();
				} else if (cmdLabel.equalsIgnoreCase("listgen")) {
					rval = new WorldListGenerators();
				} else if (cmdLabel.equalsIgnoreCase("togglespawnloaded")) {
					rval = new WorldToggleSpawnLoaded();
				} else if (cmdLabel.equalsIgnoreCase("spawnloaded")) {
					rval = new WorldToggleSpawnLoaded();
				} else if (cmdLabel.equalsIgnoreCase("keepspawnloaded")) {
					rval = new WorldToggleSpawnLoaded();
				} else if (cmdLabel.equalsIgnoreCase("difficulty")) {
					rval = new WorldDifficulty();
				} else if (cmdLabel.equalsIgnoreCase("difficult")) {
					rval = new WorldDifficulty();
				} else if (cmdLabel.equalsIgnoreCase("diff")) {
					rval = new WorldDifficulty();
				} else if (cmdLabel.equalsIgnoreCase("op")) {
					rval = new WorldOpping(true);
				} else if (cmdLabel.equalsIgnoreCase("deop")) {
					rval = new WorldOpping(false);
				} else if (cmdLabel.equalsIgnoreCase("setsave")) {
					rval = new WorldSetSaving();
				} else if (cmdLabel.equalsIgnoreCase("setsaving")) {
					rval = new WorldSetSaving();
				} else if (cmdLabel.equalsIgnoreCase("saving")) {
					rval = new WorldSetSaving();
				} else if (cmdLabel.equalsIgnoreCase("autosave")) {
					rval = new WorldSetSaving();
				} else if (cmdLabel.equalsIgnoreCase("config")) {
					rval = new WorldConfig();
				} else if (cmdLabel.equalsIgnoreCase("cfg")) {
					rval = new WorldConfig();
				} else if (cmdLabel.equalsIgnoreCase("reloadwhenempty")) {
					rval = new WorldReloadWE();
				} else if (cmdLabel.equalsIgnoreCase("reloadwe")) {
					rval = new WorldReloadWE();
				} else if (cmdLabel.equalsIgnoreCase("reloadempty")) {
					rval = new WorldReloadWE();
				} else if (cmdLabel.equalsIgnoreCase("reloadnoplayers")) {
					rval = new WorldReloadWE();
				} else if (cmdLabel.equalsIgnoreCase("formsnow")) {
					rval = new WorldFormSnow();
				} else if (cmdLabel.equalsIgnoreCase("formice")) {
					rval = new WorldFormIce();
				} else if (cmdLabel.equalsIgnoreCase("showsnow")) {
					rval = new WorldShowSnow();
				} else if (cmdLabel.equalsIgnoreCase("showrain")) {
					rval = new WorldShowRain();
				} else if (cmdLabel.equalsIgnoreCase("teleport")) {
					rval = new TeleportPortal();
				} else if (cmdLabel.equalsIgnoreCase("tp")) {
					rval = new TeleportPortal();
				} else if (cmdLabel.equalsIgnoreCase("inventory")) {
					rval = new WorldInventory();
				} else if (cmdLabel.equalsIgnoreCase("inv")) {
					rval = new WorldInventory();
				} else if (cmdLabel.equalsIgnoreCase("togglerespawn")) {
					rval = new WorldToggleRespawn();
				} else if (cmdLabel.equalsIgnoreCase("respawn")) {
					rval = new WorldToggleRespawn();
				}
			}
		} else if (cmdLabel.equalsIgnoreCase("tpp")) {
			rval = new TeleportPortal();
		}
		if (rval == null) {
			rval = new Command(null, null);
			rval.init(sender, new String[] {cmdLabel});
			rval.execute();
		} else {
			rval.init(sender, args);
			if (!rval.hasPermission()) {
				if (rval.player == null) {
					rval.sender.sendMessage("This command is only for players!");
				} else {
					rval.locmessage(Localization.COMMAND_NOPERM);
				}
			} else {
				rval.execute();
			}
		}
	}		
	
	public void execute() {
		//This is executed when no command was found
		boolean hac = false; //has available commands
		for (String command : commandNodes) {
			hac |= showUsage(command);
		}
		if (hac) {
			message(ChatColor.RED + "Unknown command: " + args[0]);
		} else {
			locmessage(Localization.COMMAND_NOPERM);
		}
	}

}
