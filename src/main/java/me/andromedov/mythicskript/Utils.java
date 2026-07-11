package me.andromedov.mythicskript;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.drops.DropExecutor;
import io.lumine.mythic.core.items.ItemExecutor;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.spawning.spawners.SpawnerManager;

public class Utils {

	public static MythicBukkit mythicMobs;
	public static MobExecutor mobManager;
	public static ItemExecutor itemManager;
	public static BukkitAPIHelper mythicHelper;
	public static DropExecutor dropExecutor;
	public static SpawnerManager spawnerManager;
	public static VolatileCodeHandler VCH;

	private Utils() {}

	/**
	 * Initializes static managers and helpers for MythicMobs integration
	 */
	public static void init() {
		mythicMobs = MythicBukkit.inst();
		mobManager = mythicMobs.getMobManager();
		itemManager = mythicMobs.getItemManager();
		mythicHelper = mythicMobs.getAPIHelper();
		dropExecutor = mythicMobs.getDropManager();
		spawnerManager = mythicMobs.getSpawnerManager();
		VCH = mythicMobs.getVolatileCodeHandler();
	}


	/**
	 * Parses targeter string and retrieves configured skill targeter
	 * @param targeterString - formatted string with @ and the name of the targeter (ex. @PIR{r=30})
	 * @return - configured SkillTargeter or null
	 */
	public static SkillTargeter parseSkillTargeter(String targeterString) {
		String search = targeterString.startsWith("@") ? targeterString.substring(1) : targeterString;
		MythicLineConfigImpl mlc  = new MythicLineConfigImpl(search);
		String name = search.contains("{") ? search.substring(0, search.indexOf('{')) : search;
		return mythicMobs.getSkillManager().getTargeter(name, mlc);
	}
}
