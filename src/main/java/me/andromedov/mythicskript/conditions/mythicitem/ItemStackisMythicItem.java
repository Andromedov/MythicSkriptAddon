package me.andromedov.mythicskript.conditions.mythicitem;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class ItemStackisMythicItem extends Condition {

	Expression<ItemStack>expr;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		expr=(Expression<ItemStack>) expressions[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
        assert e != null;
        return "@"+e.getEventName();
	}

	@Override
	public boolean check(Event event) {
		ItemStack item = expr.getSingle(event);
		if (item == null) return false;

		String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
		return mythicType != null;
	}
}