package me.andromedov.mythicskript.effects.mythicitem;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;
import me.andromedov.mythicskript.mythicitem.MythicItemHelper;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class TakeMythicItem extends Effect {
    private Expression<Number> amountExpr;
    private Expression<String> itemTypeExpr;
    private Expression<Player> playersExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        amountExpr = (Expression<Number>) expr[0];
        itemTypeExpr = (Expression<String>) expr[1];
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

        int amountToTake = MythicItemHelper.getAmount(amountExpr, event);

        for (Player p : players) {
            int remaining = amountToTake;
            ItemStack[] contents = p.getInventory().getContents();

            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null) continue;

                String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
                if (mythicType != null && mythicType.equalsIgnoreCase(mythicName)) {
                    int itemAmount = item.getAmount();
                    if (itemAmount <= remaining) {
                        remaining -= itemAmount;
                        p.getInventory().setItem(i, null);
                    } else {
                        item.setAmount(itemAmount - remaining);
                        remaining = 0;
                    }
                }
                if (remaining <= 0) break;
            }
        }
    }
}
