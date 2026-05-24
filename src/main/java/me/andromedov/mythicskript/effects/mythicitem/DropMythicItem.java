package me.andromedov.mythicskript.effects.mythicitem;

import java.util.Objects;
import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class DropMythicItem extends Effect {
    private Expression<String> itemTypeExpr;
    private Expression<Number> amountExpr;
    private Expression<Location> locationsExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemTypeExpr = (Expression<String>) expr[0];
        amountExpr = (Expression<Number>) expr[1];
        locationsExpr = (Expression<Location>) expr[2];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    protected void execute(Event event) {
        Location[] locations = locationsExpr.getAll(event);
        String mythicName = itemTypeExpr.getSingle(event);

        if (mythicName == null || locations == null || locations.length == 0) return;

        int amount = (amountExpr != null && amountExpr.getSingle(event) != null)
                ? Objects.requireNonNull(amountExpr.getSingle(event)).intValue()
                : 1;

        ItemStack item = MythicItemHelper.getGeneratedItem(mythicName, amount);
        if (item == null) return;

        for (Location loc : locations) {
            if (loc != null && loc.getWorld() != null) {
                loc.getWorld().dropItemNaturally(loc, item.clone());
            }
        }
    }
}