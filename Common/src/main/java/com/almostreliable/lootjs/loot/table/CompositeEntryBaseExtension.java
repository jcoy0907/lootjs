package com.almostreliable.lootjs.loot.table;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;

public interface CompositeEntryBaseExtension {
    LootPoolEntryContainer[] lootjs$getEntries();

    void lootjs$setEntries(LootPoolEntryContainer[] children);
}
