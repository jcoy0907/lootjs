package com.github.llytho.lootjs.loot.action;

import com.github.llytho.lootjs.core.ILootAction;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

public class WeightedAddLootAction implements ILootAction {

    private final MinMaxBounds.Ints interval;
    private final SimpleWeightedRandomList<ItemStack> weightedRandomList;
    private final boolean allowDuplicateLoot;

    public WeightedAddLootAction(MinMaxBounds.Ints interval, SimpleWeightedRandomList<ItemStack> weightedRandomList, boolean allowDuplicateLoot) {
        this.interval = interval;
        this.weightedRandomList = weightedRandomList;
        this.allowDuplicateLoot = allowDuplicateLoot;
        if (weightedRandomList.isEmpty()) {
            throw new IllegalArgumentException("Weighted list must have at least one item");
        }
    }

    @Override
    public boolean applyLootHandler(LootContext context, List<ItemStack> loot) {
        Random random = context.getLevel().getRandom();
        Collection<ItemStack> rolledItems = allowDuplicateLoot ? new ArrayList<>() : new HashSet<>();
        int lootRolls = getLootRolls(random);
        for (int i = 0; i < lootRolls; i++) {
            weightedRandomList.getRandomValue(random).ifPresent(rolledItems::add);
        }
        loot.addAll(rolledItems.stream().map(ItemStack::copy).toList());
        return true;
    }

    protected int getLootRolls(Random random) {
        int min = ObjectUtils.firstNonNull(interval.getMin(), 1);
        int max = ObjectUtils.firstNonNull(interval.getMax(), min);
        return random.nextInt(max + 1 - min) + min;
    }
}