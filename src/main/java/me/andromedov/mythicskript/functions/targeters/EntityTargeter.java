package me.andromedov.mythicskript.functions.targeters;

import java.util.HashSet;

import org.bukkit.entity.Entity;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.function.Function;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.targeters.IEntitySelector;

public class EntityTargeter extends IEntitySelector {
	Function<?>function;
	Object[][]parameters;

	public EntityTargeter(MythicLineConfig mlc,Function<?>f) {
		super(Utils.mythicMobs.getSkillManager(),mlc);

		function=f;
		parameters=new Object[1][];
	}

	@Override
	@SuppressWarnings({"removal"})
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		parameters[0]=new SkillMetadata[] {data};
		Object[] result = function.execute(parameters);
		HashSet<AbstractEntity>targets=new HashSet<>();

		if (result != null) {
            for (Object o : result) {
                if (o instanceof Entity) {
                    targets.add(BukkitAdapter.adapt((Entity) o));
                }
            }
		}
		return targets;
	}
}