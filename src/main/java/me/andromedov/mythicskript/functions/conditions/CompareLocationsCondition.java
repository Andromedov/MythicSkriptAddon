package me.andromedov.mythicskript.functions.conditions;

import org.bukkit.Location;

import ch.njol.skript.lang.function.Function;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;
import io.lumine.mythic.bukkit.BukkitAdapter;

public class CompareLocationsCondition extends Condition implements ILocationComparisonCondition {
	public CompareLocationsCondition(String line, MythicLineConfig mlc, Function<?>f) {
		super(line,mlc,f);
		parameters=new Object[2][];
	}

	@Override
	@SuppressWarnings({"removal"})
	public boolean check(AbstractLocation aCaster, AbstractLocation aTarget) {
		parameters[0]=new Location[] {BukkitAdapter.adapt(aCaster)};
		parameters[1]=new Location[] {BukkitAdapter.adapt(aTarget)};

		Object[] result = function.execute(parameters);

		if(result != null && result.length > 0 && (result[0] instanceof Boolean)) {
			return (boolean) result[0];
		}
		return false;
	}
}