package com.github.llytho.lootjs.loot.condition;

import net.minecraft.loot.LootContext;
import net.minecraft.util.ResourceLocation;

public class AnyDimension implements IExtendedLootCondition {

    private final ResourceLocation[] dimensions;

    public AnyDimension(ResourceLocation[] dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public boolean test(LootContext context) {
        ResourceLocation levelDimension = context.getLevel().dimension().location();
        for (ResourceLocation dimension : dimensions) {
            if (dimension.equals(levelDimension)) {
                return true;
            }
        }

        return false;
    }
}