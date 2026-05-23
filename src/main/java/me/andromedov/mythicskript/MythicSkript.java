package me.andromedov.mythicskript;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.skriptlang.skript.addon.SkriptAddon;

public class MythicSkript extends JavaPlugin {

	public static Plugin plugin;
	public static SkriptAddon addon;

	/**
	 * Initializes plugin; validates dependencies; registers Skript addon
	 */
	@Override
	public void onEnable() {
		plugin = this;
		Logger log = Bukkit.getLogger();

		Plugin mm = Bukkit.getPluginManager().getPlugin("MythicMobs");
		Plugin sk = Bukkit.getPluginManager().getPlugin("Skript");

		if (mm == null || !mm.isEnabled()) {
			log.severe("[MythicSkriptAddon] MythicMobs not found or not enabled - addon disabled.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		if (sk == null || !sk.isEnabled()) {
			log.severe("[MythicSkriptAddon] Skript not found or not enabled - addon disabled.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		addon = ch.njol.skript.Skript.instance().registerAddon(MythicSkript.class, "MythicSkriptAddon");

		addon.loadModules(new MythicSkriptModule());

		log.info("[MythicSkriptAddon] Successfully loaded");
	}

	@Override
	public void onDisable() {
		Bukkit.getLogger().info("[MythicSkriptAddon] Disabled.");
		plugin = null;
		addon  = null;
	}
}
