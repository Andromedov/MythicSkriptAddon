package me.andromedov.mythicskript.effects.mythicitem;

import java.util.Optional;

import javax.annotation.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Expression;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import me.andromedov.mythicskript.Utils;

public class MythicItemHelper {

    /**
     * Generates Bukkit ItemStack on the basis of MythicItem name and amount.
     * @param mythicName name of MythicItem
     * @param amount amount of ItemStack
     * @return ItemStack or null if MythicItem not found or name is null
     */
    @Nullable
    public static ItemStack getGeneratedItem(String mythicName, int amount) {
        if (mythicName == null) return null;

        Optional<MythicItem> optMythicItem = Utils.itemManager.getItem(mythicName);
        return optMythicItem.map(mythicItem -> BukkitAdapter.adapt(mythicItem.generateItemStack(amount))).orElse(null);
    }

    /**
     * @param amountExpr Expression of amount
     * @param event The event
     * @return Amount of ItemStack, defaults to 1 if null
     */
    public static int getAmount(@Nullable Expression<Number> amountExpr, Event event) {
        if (amountExpr == null) return 1;
        Number raw = amountExpr.getSingle(event);
        return raw != null ? raw.intValue() : 1;
    }
}