package me.andromedov.mythicskript.conditions.mythicitem;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class ItemStackisMythicItem extends Condition {

	private Expression<ItemStack> expr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		expr = (Expression<ItemStack>) expressions[0];
		setNegated(matchedPattern == 1);
		return true;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
	}

	@Override
	public boolean check(Event event) {
		ItemStack item = expr.getSingle(event);
		if (item == null) return isNegated();

		String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
		boolean isMythic = mythicType != null;

		return isMythic ^ isNegated();
	}
}
