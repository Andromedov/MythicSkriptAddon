package me.andromedov.mythicskript.effects.mythicitem;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import me.andromedov.mythicskript.Utils;

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
    public String toString(@Nullable Event e, boolean debug) {
        return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
    }

    @Override
    protected void execute(Event event) {
        String mythicName = itemTypeExpr.getSingle(event);
        Player[] players = playersExpr.getAll(event);

        if (mythicName == null || players == null || players.length == 0) return;

        int amount = (amountExpr != null && amountExpr.getSingle(event) != null)
                ? Objects.requireNonNull(amountExpr.getSingle(event)).intValue()
                : 1;

        Optional<MythicItem> optMythicItem = Utils.itemManager.getItem(mythicName);
        if (optMythicItem.isEmpty()) return;

        ItemStack item = BukkitAdapter.adapt(optMythicItem.get().generateItemStack(amount));

        for (Player p : players) {
            p.getInventory().addItem(item.clone());
        }
    }
}