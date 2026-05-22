package com.gmail.berndivader.mythicskript.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicskript.MythicSkript;
import com.gmail.berndivader.mythicskript.Utils;
import com.gmail.berndivader.mythicskript.events.skript.MythicSkriptSpawnEvent;
import com.gmail.berndivader.mythicskript.events.skript.MythicSkriptSpawnerSpawnEvent;

import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMechanic;

public class BukkitEvents implements Listener {

	public BukkitEvents() {
		MythicSkript.plugin.getServer().getPluginManager().registerEvents(this,MythicSkript.plugin);
	}

	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent e) {
		if (!e.getSpawnReason().equals(SpawnReason.CUSTOM) || e.isCancelled()) return;

		Entity bukkitEntity = e.getEntity();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!Utils.mythicHelper.isMythicMob(bukkitEntity)) return;

				ActiveMob am = Utils.mythicHelper.getMythicMobInstance(bukkitEntity);
				if (am == null) return;

				// Triggers spawner and spawn events for active mob
				if (am.getSpawner() != null) {
					Bukkit.getServer().getPluginManager()
							.callEvent(new MythicSkriptSpawnerSpawnEvent(am.getSpawner(), am));
				}
				Bukkit.getServer().getPluginManager()
						.callEvent(new MythicSkriptSpawnEvent(am));
			}
		}.runTaskLater(MythicSkript.plugin, 1L);
	}

	@EventHandler
	public void onMythicMobsCustomMechanicsLoad(MythicMechanicLoadEvent e) {
		String name = e.getMechanicName().toLowerCase();
		if (name.equals("skriptskill") || name.equals("skfunction")) {
			SkillMechanic skill = new MythicSkriptSkill(e.getContainer(), e.getConfig());
			e.register(skill);
		}
	}


	@EventHandler
	public void onMythicMobsCustomConditionsLoad(MythicConditionLoadEvent e) {
		String name = e.getConditionName().toLowerCase();
		// Creates and assigns custom condition based on name
		SkillCondition condition = switch (name) {
			case "skriptcondition"      -> new MythicSkriptCondition(e.getConditionName(), e.getConfig());
			case "skriptspawncondition" -> new MythicSkriptSpawnCondition(e.getConditionName(), e.getConfig());
			case "skripttargetcondition"-> new MythicSkriptTargetCondition(e.getConditionName(), e.getConfig());
			default                     -> null;
		};
		if (condition != null) e.register(condition);
	}
}
