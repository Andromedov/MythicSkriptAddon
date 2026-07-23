package me.andromedov.mythicskript.functions.mechanics;

import me.andromedov.mythicskript.classes.ColorData;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.function.Function;
import ch.njol.skript.lang.function.Functions;
import ch.njol.skript.util.Color;
import org.skriptlang.skript.common.function.Parameter;
import org.skriptlang.skript.common.function.Parameters;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillMechanic;

public class SkriptfunctionMechanic extends SkillMechanic implements INoTargetSkill,ITargetedEntitySkill,ITargetedLocationSkill {
	private final Function<?> function;
	private final ConfigArgument[] configArguments;
	private final int parameterCount;
	private final int dataPos;
	private final int locationPos;
	private final int entityPos;
	private final String name;
	private final boolean valid;

	public SkriptfunctionMechanic(SkillMechanic skill,MythicLineConfig mlc) {
		super(skill.getManager(),skill.getFile(), mlc.getLine(), mlc);

		name=mlc.getString("name","");
		function=Functions.getGlobalFunction(name);
		int foundDataPos = -1;
		int foundLocationPos = -1;
		int foundEntityPos = -1;
		int foundParameterCount = 0;
		boolean foundValid = function != null;

		if(function!=null) {
			Parameters params = function.signature().parameters();
			foundParameterCount = params.size();
			configArguments = new ConfigArgument[params.size()];

			for(int i=0;i<params.size();i++) {
				Parameter<?> parameter = params.get(i);
				Class<?> type = parameter.type();
				if (type != null) {
					if (SkillMetadata.class.isAssignableFrom(type)) {
						foundDataPos = i;
					} else if (Location.class.isAssignableFrom(type)) {
						foundLocationPos = i;
					} else if (Entity.class.isAssignableFrom(type)) {
						foundEntityPos = i;
					} else if (isSupportedConfigType(type)) {
						String rawValue = mlc.getString(parameter.name(), null);
						if (rawValue == null) {
							Skript.warning("Missing skfunction config parameter '" + parameter.name()
									+ "' for function " + name);
							foundValid = false;
						} else {
							configArguments[i] = new ConfigArgument(
									parameter.name(),
									type,
									PlaceholderString.of(rawValue)
							);
						}
					} else {
						Skript.warning("Unsupported skfunction parameter type "
								+ type.getSimpleName() + " for parameter '" + parameter.name()
								+ "' in function " + name);
						foundValid = false;
					}
				}
			}
		} else {
			configArguments = new ConfigArgument[0];
			Skript.warning("Cant find function "+name);
		}

		parameterCount = foundParameterCount;
		dataPos = foundDataPos;
		locationPos = foundLocationPos;
		entityPos = foundEntityPos;
		valid = foundValid;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult castAtLocation(SkillMetadata meta, AbstractLocation aLocation) {
		Object[][] parameters = createParameters(meta);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if(locationPos>-1) parameters[locationPos]=new Location[] {BukkitAdapter.adapt(aLocation)};
		if(entityPos>-1) parameters[entityPos]=new Entity[0];
		if (!populateConfigArguments(parameters, meta, null, aLocation)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity aEntity) {
		Object[][] parameters = createParameters(meta);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if(locationPos>-1) parameters[locationPos]=new Location[0];
		if(entityPos>-1) parameters[entityPos]=new Entity[] {aEntity.getBukkitEntity()};
		if (!populateConfigArguments(parameters, meta, aEntity, null)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult cast(SkillMetadata meta) {
		Object[][] parameters = createParameters(meta);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if(locationPos>-1) parameters[locationPos]=new Location[0];
		if(entityPos>-1) parameters[entityPos]=new Entity[0];
		if (!populateConfigArguments(parameters, meta, null, null)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	private Object[][] createParameters(SkillMetadata meta) {
		if (!valid || function == null) {
			return null;
		}

		Object[][] parameters = new Object[parameterCount][];
		if(dataPos>-1) parameters[dataPos]=new SkillMetadata[] {meta};
		return parameters;
	}

	private boolean populateConfigArguments(
			Object[][] parameters,
			SkillMetadata meta,
			AbstractEntity targetEntity,
			AbstractLocation targetLocation
	) {
		for (int i = 0; i < configArguments.length; i++) {
			ConfigArgument argument = configArguments[i];
			if (argument == null) continue;

			String value;
			if (targetEntity != null) {
				value = argument.value().get(meta, targetEntity);
			} else if (targetLocation != null) {
				value = argument.value().get(meta, targetLocation);
			} else {
				value = argument.value().get(meta);
			}

			try {
				parameters[i] = new Object[] {convert(value, argument.type())};
			} catch (IllegalArgumentException exception) {
				Skript.warning("Invalid value '" + value + "' for skfunction parameter '"
						+ argument.name() + "' in function " + name + ": " + exception.getMessage());
				return false;
			}
		}
		return true;
	}

	private static boolean isSupportedConfigType(Class<?> type) {
		return type == String.class
				|| type == Boolean.class
				|| Number.class.isAssignableFrom(type)
				|| ColorData.class.isAssignableFrom(type)
				|| Color.class.isAssignableFrom(type);
	}

	private static Object convert(String value, Class<?> type) {
		if (type == String.class) return value;
		if (type == Boolean.class) {
			if ("true".equalsIgnoreCase(value)) return true;
			if ("false".equalsIgnoreCase(value)) return false;
			throw new IllegalArgumentException("expected true or false");
		}
		if (ColorData.class.isAssignableFrom(type) || Color.class.isAssignableFrom(type)) {
			return ColorData.parse(value);
		}
		try {
			if (type == Integer.class) return Integer.valueOf(value);
			if (type == Long.class) return Long.valueOf(value);
			if (type == Float.class) return Float.valueOf(value);
			if (type == Short.class) return Short.valueOf(value);
			if (type == Byte.class) return Byte.valueOf(value);
			return Double.valueOf(value);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("expected a number", exception);
		}
	}

	private record ConfigArgument(String name, Class<?> type, PlaceholderString value) {}
}
