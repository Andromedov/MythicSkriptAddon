package me.andromedov.mythicskript.functions;

import me.andromedov.mythicskript.functions.conditions.*;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.MythicSkript;
import me.andromedov.mythicskript.functions.conditions.*;
import me.andromedov.mythicskript.functions.drops.ItemDrop;
import me.andromedov.mythicskript.functions.drops.MessageDrop;
import me.andromedov.mythicskript.functions.mechanics.SkriptfunctionMechanic;
import me.andromedov.mythicskript.functions.targeters.EntityTargeter;
import me.andromedov.mythicskript.functions.targeters.LocationTargeter;

import ch.njol.skript.lang.function.Function;
import org.skriptlang.skript.common.function.Parameters;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;

public class Functions implements Listener {

	public Functions() {
		MythicSkript.plugin.getServer().getPluginManager()
				.registerEvents(this, MythicSkript.plugin);
	}

	private Function<?> getFunctionByConfig(MythicLineConfig mlc) {
		String fname = mlc.getString(new String[]{"name", "n"}, "");
		if (fname.isBlank()) return null;
		return ch.njol.skript.lang.function.Functions.getFunction(fname, null);
	}

	/**
	 * Registers custom mechanic for specific names
	 */
	@SuppressWarnings("unused")
	@EventHandler
	public void onMechanicLoad(MythicMechanicLoadEvent e) {
		switch (e.getMechanicName().toLowerCase()) {
			case "skriptskill", "skfunction" ->
					e.register(new SkriptfunctionMechanic(e.getContainer(), e.getConfig()));
		}
	}

	/**
	 * Handles condition loading with parameter validation
	 */
	@SuppressWarnings("unused")
	@EventHandler
	public void onConditionLoad(MythicConditionLoadEvent e) {
		if (!e.getConditionName().equalsIgnoreCase("skfunction")) return;

		Function<?> function = getFunctionByConfig(e.getConfig());
		if (function == null) return;

		Parameters params = function.getSignature().parameters();
		if (params.size() == 0) return;

		Class<?> p0 = params.get(0).type();

		if (params.size() == 1) {
			// Registers condition for a single-parameter entity or location
			if (isEntity(p0)) {
				e.register(new EntityCondition(e.getConditionName(), e.getConfig(), function));
			} else if (isLocation(p0)) {
				e.register(new LocationCondition(e.getConditionName(), e.getConfig(), function));
			}
			return;
		}

		Class<?> p1 = params.get(1).type();

		// Registers conditions for entity and location comparisons
		if (isEntity(p0) && isEntity(p1)) {
			e.register(new CompareEntitiesCondition(e.getConditionName(), e.getConfig(), function));
		} else if (isLocation(p0) && isLocation(p1)) {
			e.register(new CompareLocationsCondition(e.getConditionName(), e.getConfig(), function));
		} else if (isEntity(p0) && isLocation(p1)) {
			e.register(new CompareEntityLocationCondition(e.getConditionName(), e.getConfig(), function, true));
		} else if (isLocation(p0) && isEntity(p1)) {
			e.register(new CompareEntityLocationCondition(e.getConditionName(), e.getConfig(), function, false));
		}
	}

	/**
	 * Handles targeter loading; registers based on return type
	 */
	@SuppressWarnings("unused")
	@EventHandler
	public void onTargeterLoad(MythicTargeterLoadEvent e) {
		if (!e.getTargeterName().equalsIgnoreCase("skfunction")) return;

		Function<?> function = getFunctionByConfig(e.getConfig());
		if (function == null) return;

		Class<?> returnType = function.type();
		if (returnType == null) return;

		// Registers targeter based on function return type
		if (isEntity(returnType)) {
			e.register(new EntityTargeter(e.getConfig(), function));
		} else if (isLocation(returnType)) {
			e.register(new LocationTargeter(e.getConfig(), function));
		}
	}

	@SuppressWarnings("unused")
	@EventHandler
	public void onDropLoad(MythicDropLoadEvent e) {
		switch (e.getDropName().toLowerCase()) {
			case "skfunction", "skriptfunction" -> {
				Function<?> function = getFunctionByConfig(e.getConfig());
				if (function == null) return;

				Class<?> returnType = function.type();
				if (returnType != null && ItemStack.class.isAssignableFrom(returnType)) {
					e.register(new ItemDrop(e.getDropName(), e.getConfig(), function));
				} else {
					e.register(new MessageDrop(e.getDropName(), e.getConfig(), function));
				}
			}
		}
	}

	private static boolean isEntity(Class<?> type) {
		return Entity.class.isAssignableFrom(type);
	}

	private static boolean isLocation(Class<?> type) {
		return Location.class.isAssignableFrom(type);
	}
}
