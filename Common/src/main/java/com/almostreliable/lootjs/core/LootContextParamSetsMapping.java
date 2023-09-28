package com.almostreliable.lootjs.core;

import com.google.common.collect.ImmutableMap;
import net.minecraft.Util;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.Map;

public class LootContextParamSetsMapping {
    public static final Map<LootContextParamSet, LootContextType> PSETS_TO_TYPE = ImmutableMap
            .<LootContextParamSet, LootContextType>builder()
            .put(LootContextParamSets.BLOCK, LootContextType.BLOCK)
            .put(LootContextParamSets.ENTITY, LootContextType.ENTITY)
            .put(LootContextParamSets.CHEST, LootContextType.CHEST)
            .put(LootContextParamSets.FISHING, LootContextType.FISHING)
            .put(LootContextParamSets.GIFT, LootContextType.GIFT)
            .put(LootContextParamSets.ARCHAEOLOGY, LootContextType.ARCHAEOLOGY)
            .put(LootContextParamSets.PIGLIN_BARTER, LootContextType.PIGLIN_BARTER)
            .put(LootContextParamSets.ADVANCEMENT_ENTITY, LootContextType.ADVANCEMENT_ENTITY)
            .put(LootContextParamSets.ADVANCEMENT_REWARD, LootContextType.ADVANCEMENT_REWARD)
            .build();

    public static final Map<LootContextType, LootContextParamSet> TYPE_TO_PSETS = Util.make(() -> {
        var builder = ImmutableMap.<LootContextType, LootContextParamSet>builder();
        for (var entry : PSETS_TO_TYPE.entrySet()) {
            builder.put(entry.getValue(), entry.getKey());
        }

        return builder.build();
    });
}
