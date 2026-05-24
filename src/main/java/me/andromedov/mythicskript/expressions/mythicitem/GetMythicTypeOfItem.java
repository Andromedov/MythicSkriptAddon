package me.andromedov.mythicskript.expressions.mythicitem;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class GetMythicTypeOfItem extends SimpleExpression<String> {
    private Expression<ItemStack> itemExpr;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemExpr = (Expression<ItemStack>) expr[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    protected String[] get(Event event) {
        ItemStack item = itemExpr.getSingle(event);
        if (item == null) return null;

        String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
        if (mythicType != null) {
            return new String[]{mythicType};
        }

        return null;
    }
}