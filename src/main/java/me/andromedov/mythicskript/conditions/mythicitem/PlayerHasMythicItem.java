package me.andromedov.mythicskript.conditions.mythicitem;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import me.andromedov.mythicskript.Utils;
import me.andromedov.mythicskript.effects.mythicitem.MythicItemHelper;

import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class PlayerHasMythicItem extends Condition {
    private Expression<Player> playerExpr;
    private Expression<String> itemTypeExpr;
    private Expression<Number> amountExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expr, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        playerExpr = (Expression<Player>) expr[0];
        itemTypeExpr = (Expression<String>) expr[1];
        amountExpr = (Expression<Number>) expr[2];
        return true;
    }

    @Override
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    public boolean check(Event event) {
        Player player = playerExpr.getSingle(event);
        String mythicName = itemTypeExpr.getSingle(event);

        if (player == null || mythicName == null) return false;

        int requiredAmount = MythicItemHelper.getAmount(amountExpr, event);
        int foundAmount = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            String mythicType = Utils.itemManager.getMythicTypeFromItem(item);
            if (mythicType != null && mythicType.equalsIgnoreCase(mythicName)) {
                foundAmount += item.getAmount();
                if (foundAmount >= requiredAmount) {
                    return true;
                }
            }
        }

        return false;
    }
}