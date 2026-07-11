package me.andromedov.mythicskript;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.skriptlang.skript.addon.SkriptAddon;
import io.lumine.mythic.core.skills.CustomComponentRegistry;

import java.util.ArrayList;

public class MythicSkript extends JavaPlugin {

	public static Plugin plugin;
	public static SkriptAddon addon;

	/**
	 * Initializes plugin; validates dependencies; registers Skript addon
	 */
	@Override
	public void onEnable() {
		plugin = this;
		Logger log = plugin.getLogger();

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

		org.bstats.bukkit.Metrics metrics = new org.bstats.bukkit.Metrics(this, 31569);

		addon = ch.njol.skript.Skript.instance().registerAddon(MythicSkript.class, "MythicSkriptAddon");

		addon.loadModules(new MythicSkriptModule());

        CustomComponentRegistry placeholderRegistry = new CustomComponentRegistry(this, new ArrayList<>())
                .registerCustomComponent(
                        CustomComponentRegistry.MythicComponentType.PLACEHOLDER,
                        "me.andromedov.mythicskript.placeholders"
                );

		log.info("[MythicSkriptAddon] Successfully loaded");
	}

	@Override
	public void onDisable() {
		plugin.getLogger().info("[MythicSkriptAddon] Disabled.");
		plugin = null;
		addon = null;
	}
}
