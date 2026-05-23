package com.gmail.berndivader.mythicskript.effects.mobitems;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.LootBag;

public class SetPhysicalLootForLootBag extends Effect {
	Expression<LootBag> exprBag;
	Expression<ItemStack> exprItem;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expr, int arg1, Kleenean arg2, ParseResult arg3) {
		if (expr.length > 1) {
			exprItem = expr[1] != null
					? (Expression<ItemStack>) expr[1]
					: (Expression<ItemStack>) expr[2];
		}
		exprBag = (Expression<LootBag>) expr[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return getClass().getSimpleName() + (e != null ? "@" + e.getEventName() : "");
	}

	@Override
	protected void execute(Event event) {
		LootBag lootBag = exprBag.getSingle(event);
		if (lootBag == null) return;

		List<Drop> newDrops = new ArrayList<>();
		if (exprItem != null) {
			if (exprItem.isSingle()) {
				AbstractItemStack item = BukkitAdapter.adapt(exprItem.getSingle(event));
				newDrops.add(new SimpleItemDrop(item));
			} else {
				for (ItemStack stack : exprItem.getArray(event)) {
					newDrops.add(new SimpleItemDrop(BukkitAdapter.adapt(stack)));
				}
			}
		}
		lootBag.setLootTable(newDrops);
	}

	private static class SimpleItemDrop extends Drop implements IItemDrop {

		private final AbstractItemStack item;

		SimpleItemDrop(AbstractItemStack item) {
			super("MMSK_DROP", null);
			this.item = item;
			this.setAmount(1);
		}

		@Override
		public AbstractItemStack getDrop(DropMetadata data, double amount) {
			return item;
		}
	}
}
