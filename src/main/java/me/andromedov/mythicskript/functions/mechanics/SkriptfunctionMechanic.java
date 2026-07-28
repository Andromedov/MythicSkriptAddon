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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SkriptfunctionMechanic extends SkillMechanic implements INoTargetSkill,ITargetedEntitySkill,ITargetedLocationSkill {
	private final Function<?> function;
	private final ConfigArgument[] configArguments;
	private final AutoArgument[] autoArguments;
	private final int parameterCount;
	private final String name;
	private final boolean valid;

	public SkriptfunctionMechanic(SkillMechanic skill,MythicLineConfig mlc) {
		super(skill.getManager(),skill.getFile(), mlc.getLine(), mlc);
		this.setAsyncSafe(false);

		name=mlc.getString(new String[]{"name", "n"},"");
		function=Functions.getGlobalFunction(name);
		List<AutoArgument> foundAutoArguments = new ArrayList<>();
		int foundParameterCount = 0;
		boolean foundValid = function != null;

		if(function!=null) {
			Parameters params = function.signature().parameters();
			foundParameterCount = params.size();
			configArguments = new ConfigArgument[params.size()];

			for(int i=0;i<params.size();i++) {
				Parameter<?> parameter = params.get(i);
				Class<?> type = parameter.type();
				if (type == null) {
					Skript.warning("Unknown skfunction parameter type for parameter '" + parameter.name()
							+ "' in function " + name);
					foundValid = false;
				} else {
					AutoKind autoKind = getAutoKind(type);
					if (autoKind != null) {
						foundAutoArguments.add(new AutoArgument(i, type, autoKind));
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
			Skript.warning("Can't find function " + name);
		}

		parameterCount = foundParameterCount;
		autoArguments = foundAutoArguments.toArray(AutoArgument[]::new);
		valid = foundValid;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult castAtLocation(SkillMetadata meta, AbstractLocation aLocation) {
		Location target = BukkitAdapter.adapt(aLocation);
		Object[][] parameters = createParameters(meta, null, target);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if (!populateConfigArguments(parameters, meta, null, aLocation)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult castAtEntity(SkillMetadata meta, AbstractEntity aEntity) {
		Object[][] parameters = createParameters(meta, aEntity.getBukkitEntity(), null);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if (!populateConfigArguments(parameters, meta, aEntity, null)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	@Override
	@SuppressWarnings({"removal"})
	public SkillResult cast(SkillMetadata meta) {
		Object[][] parameters = createParameters(meta, null, null);
		if (parameters == null) return SkillResult.INVALID_CONFIG;

		if (!populateConfigArguments(parameters, meta, null, null)) {
			return SkillResult.INVALID_CONFIG;
		}

		function.execute(parameters);
		return SkillResult.SUCCESS;
	}

	private Object[][] createParameters(SkillMetadata meta, Entity targetEntity, Location targetLocation) {
		if (!valid || function == null) {
			return null;
		}

		Object[][] parameters = new Object[parameterCount][];
		for (AutoArgument argument : autoArguments) {
			Object value = switch (argument.kind()) {
				case SKILL_DATA -> meta;
				case ENTITY -> targetEntity;
				case LOCATION -> targetLocation;
			};

			if (value != null && !argument.type().isInstance(value)) {
				Skript.warning("Invalid target type for skfunction parameter at position "
						+ (argument.position() + 1) + " in function " + name + ": expected "
						+ argument.type().getSimpleName() + ", got " + value.getClass().getSimpleName());
				return null;
			}

			parameters[argument.position()] = createTypedArray(argument.type(), value);
		}
		return parameters;
	}

	private static Object[] createTypedArray(Class<?> type, Object value) {
		Object[] values = (Object[]) Array.newInstance(type, value == null ? 0 : 1);
		if (value != null) {
			values[0] = value;
		}
		return values;
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
				|| isSupportedNumberType(type)
				|| type == ColorData.class
				|| type == Color.class;
	}

	private static boolean isSupportedNumberType(Class<?> type) {
		return type == Number.class
				|| type == Double.class
				|| type == Integer.class
				|| type == Long.class
				|| type == Float.class
				|| type == Short.class
				|| type == Byte.class;
	}

	private static AutoKind getAutoKind(Class<?> type) {
		if (SkillMetadata.class.isAssignableFrom(type)) return AutoKind.SKILL_DATA;
		if (Location.class.isAssignableFrom(type)) return AutoKind.LOCATION;
		if (Entity.class.isAssignableFrom(type)) return AutoKind.ENTITY;
		return null;
	}

	static Object convert(String value, Class<?> type) {
		if (type == String.class) return value;
		String normalized = value == null ? null : value.trim();
		if (type == Boolean.class) {
			if ("true".equalsIgnoreCase(normalized)) return true;
			if ("false".equalsIgnoreCase(normalized)) return false;
			throw new IllegalArgumentException("expected true or false");
		}
		if (type == ColorData.class || type == Color.class) {
			return ColorData.parse(normalized);
		}
		try {
			if (type == Integer.class) return Integer.valueOf(normalized);
			if (type == Long.class) return Long.valueOf(normalized);
			if (type == Float.class) return Float.valueOf(normalized);
			if (type == Short.class) return Short.valueOf(normalized);
			if (type == Byte.class) return Byte.valueOf(normalized);
			return Double.valueOf(normalized);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("expected a number", exception);
		}
	}

	private record ConfigArgument(String name, Class<?> type, PlaceholderString value) {}
	private record AutoArgument(int position, Class<?> type, AutoKind kind) {}
	private enum AutoKind { SKILL_DATA, ENTITY, LOCATION }
}
