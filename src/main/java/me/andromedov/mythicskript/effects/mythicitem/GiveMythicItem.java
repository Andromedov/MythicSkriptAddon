package me.andromedov.mythicskript.effects.mythicitem;

import java.util.HashMap;

import me.andromedov.mythicskript.mythicitem.MythicItemHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class GiveMythicItem extends Effect {
    private Expression<String> itemTypeExpr;
    private Expression<Number> amountExpr;
    private Expression<Player> playersExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        itemTypeExpr = (Expression<String>) expr[0];
        amountExpr = (Expression<Number>) expr[1];
        playersExpr = (Expression<Player>) expr[2];
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    protected void execute(Event event) {
        Player[] players = playersExpr.getAll(event);
        String mythicName = itemTypeExpr.getSingle(event);

        if (players == null || players.length == 0 || mythicName == null) return;

        int amount = MythicItemHelper.getAmount(amountExpr, event);

        ItemStack item = MythicItemHelper.getGeneratedItem(mythicName, amount);
        if (item == null) return;

        for (Player p : players) {
            HashMap<Integer, ItemStack> leftovers = p.getInventory().addItem(item.clone());
            if (!leftovers.isEmpty()) {
                for (ItemStack leftoverItem : leftovers.values()) {
                    p.getWorld().dropItemNaturally(p.getLocation(), leftoverItem);
                }
            }
        }
    }
}
