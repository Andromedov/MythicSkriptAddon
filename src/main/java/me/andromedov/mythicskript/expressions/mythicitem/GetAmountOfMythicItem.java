package me.andromedov.mythicskript.expressions.mythicitem;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class GetAmountOfMythicItem extends SimpleExpression<Number> {
    private Expression<String> itemTypeExpr;
    private Expression<Player> playerExpr;

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemTypeExpr = (Expression<String>) expr[0];
        playerExpr = (Expression<Player>) expr[1];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    protected Number[] get(Event event) {
        String mythicName = itemTypeExpr.getSingle(event);
        Player player = playerExpr.getSingle(event);

        if (mythicName == null || player == null) return new Number[]{0};

        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
            if (mythicType != null && mythicType.equalsIgnoreCase(mythicName)) {
                count += item.getAmount();
            }
        }

        return new Number[]{count};
    }
}
