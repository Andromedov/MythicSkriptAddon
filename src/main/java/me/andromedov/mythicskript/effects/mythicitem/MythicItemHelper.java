package me.andromedov.mythicskript.effects.mythicitem;

import java.util.Optional;

import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import me.andromedov.mythicskript.Utils;

public class MythicItemHelper {
    @Nullable
    public static ItemStack getGeneratedItem(String mythicName, int amount) {
        if (mythicName == null) return null;

        Optional<MythicItem> optMythicItem = Utils.itemManager.getItem(mythicName);
        return optMythicItem.map(mythicItem -> BukkitAdapter.adapt(mythicItem.generateItemStack(amount))).orElse(null);
    }
}