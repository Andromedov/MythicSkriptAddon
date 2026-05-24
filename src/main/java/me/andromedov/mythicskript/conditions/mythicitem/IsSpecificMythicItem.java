package me.andromedov.mythicskript.conditions.mythicitem;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class IsSpecificMythicItem extends Condition {
    private Expression<ItemStack> itemExpr;
    private Expression<String> nameExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemExpr = (Expression<ItemStack>) expr[0];
        nameExpr = (Expression<String>) expr[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    public boolean check(Event event) {
        ItemStack item = itemExpr.getSingle(event);
        String requiredName = nameExpr.getSingle(event);

        if (item == null || requiredName == null) return false;

        String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
        if (mythicType == null) return false;

        return mythicType.equalsIgnoreCase(requiredName);
    }
}