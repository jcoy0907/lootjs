package com.almostreliable.lootjs.loot.table;

import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public interface LootItemConditionalFunctionExtension {
    LootItemCondition[] lootjs$getConditions();

    void lootjs$setConditions(LootItemCondition[] conditions);
}
