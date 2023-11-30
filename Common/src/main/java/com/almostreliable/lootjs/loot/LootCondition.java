package com.almostreliable.lootjs.loot;

import com.almostreliable.lootjs.LootJS;
import com.almostreliable.lootjs.core.filters.ItemFilter;
import com.almostreliable.lootjs.core.filters.Resolver;
import com.almostreliable.lootjs.loot.condition.builder.DamageSourcePredicateBuilder;
import com.almostreliable.lootjs.loot.condition.builder.EntityPredicateBuilder;
import com.almostreliable.lootjs.loot.condition.*;
import com.almostreliable.lootjs.loot.condition.builder.DistancePredicateBuilder;
import com.almostreliable.lootjs.util.Utils;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LootCondition {

    public static LootItemCondition matchMainHand(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.MAINHAND, filter);
    }

    public static LootItemCondition matchOffHand(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.OFFHAND, filter);
    }

    public static LootItemCondition matchHead(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.HEAD, filter);
    }

    public static LootItemCondition matchChest(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.CHEST, filter);
    }

    public static LootItemCondition matchLegs(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.LEGS, filter);
    }

    public static LootItemCondition matchFeet(ItemFilter filter) {
        return new MatchEquipmentSlot(EquipmentSlot.FEET, filter);
    }

    public static LootItemCondition matchEquip(EquipmentSlot slot, ItemFilter filter) {
        return new MatchEquipmentSlot(slot, filter);
    }

    public static LootItemCondition survivesExplosion() {
        return ExplosionCondition.survivesExplosion().build();
    }

    public static LootItemCondition timeCheck(long period, int min, int max) {
        return new TimeCheck.Builder(IntRange.range(min, max)).setPeriod(period).build();
    }

    public static LootItemCondition timeCheck(int min, int max) {
        return timeCheck(24000L, min, max);
    }

    public static LootItemCondition weatherCheck(Map<String, Boolean> map) {
        Boolean isRaining = map.getOrDefault("raining", null);
        Boolean isThundering = map.getOrDefault("thundering", null);
        return new WeatherCheck.Builder().setRaining(isRaining).setThundering(isThundering).build();
    }

    public static LootItemCondition weatherCheck(Boolean raining, Boolean thundering) {
        return new WeatherCheck.Builder().setRaining(raining).setThundering(thundering).build();
    }

    public static LootItemCondition randomChance(float value) {
        return LootItemRandomChanceCondition.randomChance(value).build();
    }

    public static LootItemCondition randomChanceWithLooting(float value, float looting) {
        // wtf are this class names
        return LootItemRandomChanceWithLootingCondition.randomChanceAndLootingBoost(value, looting).build();
    }

    public static LootItemCondition randomChanceWithEnchantment(@Nullable Enchantment enchantment, float[] chances) {
        if (enchantment == null) {
            throw new IllegalArgumentException("Enchant not found");
        }

        return new MainHandTableBonus(enchantment, chances);
    }

    public static LootItemCondition biome(Resolver... resolvers) {
        List<ResourceKey<Biome>> biomes = new ArrayList<>();
        List<TagKey<Biome>> tagKeys = new ArrayList<>();

        for (Resolver resolver : resolvers) {
            if (resolver instanceof Resolver.ByEntry byEntry) {
                biomes.add(byEntry.resolve(Registries.BIOME));
            } else if (resolver instanceof Resolver.ByTagKey byTagKey) {
                tagKeys.add(byTagKey.resolve(Registries.BIOME));
            }
        }

        return new BiomeCheck(biomes, tagKeys);
    }

    public static LootItemCondition anyBiome(Resolver... resolvers) {
        List<ResourceKey<Biome>> biomes = new ArrayList<>();
        List<TagKey<Biome>> tagKeys = new ArrayList<>();

        for (Resolver resolver : resolvers) {
            if (resolver instanceof Resolver.ByEntry byEntry) {
                biomes.add(byEntry.resolve(Registries.BIOME));
            } else if (resolver instanceof Resolver.ByTagKey byTagKey) {
                tagKeys.add(byTagKey.resolve(Registries.BIOME));
            }
        }
        return new AnyBiomeCheck(biomes, tagKeys);
    }

    public static LootItemCondition anyDimension(ResourceLocation... dimensions) {
        return new AnyDimension(dimensions);
    }

    public static LootItemCondition anyStructure(String[] idOrTags, boolean exact) {
        AnyStructure.Builder builder = new AnyStructure.Builder();
        for (String s : idOrTags) {
            builder.add(s);
        }
        return builder.build(exact);
    }

    public static LootItemCondition lightLevel(int min, int max) {
        return new IsLightLevel(min, max);
    }

    public static LootItemCondition killedByPlayer() {
        return LootItemKilledByPlayerCondition.killedByPlayer().build();
    }

    public static LootItemCondition matchBlockState(Block block, Map<String, String> propertyMap) {
        StatePropertiesPredicate.Builder properties = Utils.createProperties(block, propertyMap);
        return new LootItemBlockStatePropertyCondition.Builder(block).setProperties(properties).build();
    }

    public static LootItemCondition matchEntity(Consumer<EntityPredicateBuilder> action) {
        EntityPredicateBuilder builder = new EntityPredicateBuilder();
        action.accept(builder);
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
                builder.build()).build();
    }

    public static LootItemCondition matchKiller(Consumer<EntityPredicateBuilder> action) {
        EntityPredicateBuilder builder = new EntityPredicateBuilder();
        action.accept(builder);
        return LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.KILLER,
                builder.build()).build();
    }

    public static LootItemCondition matchDirectKiller(Consumer<EntityPredicateBuilder> action) {
        EntityPredicateBuilder builder = new EntityPredicateBuilder();
        action.accept(builder);
        return LootItemEntityPropertyCondition
                .hasProperties(LootContext.EntityTarget.DIRECT_KILLER, builder.build())
                .build();
    }

    public static LootItemCondition matchPlayer(Consumer<EntityPredicateBuilder> action) {
        EntityPredicateBuilder builder = new EntityPredicateBuilder();
        action.accept(builder);
        return new MatchPlayer(builder.build());
    }

    public static LootItemCondition matchDamageSource(Consumer<DamageSourcePredicateBuilder> action) {
        DamageSourcePredicateBuilder builder = new DamageSourcePredicateBuilder();
        action.accept(builder);
        return builder.build();
    }

    public static LootItemCondition distanceToKiller(MinMaxBounds.Doubles bounds) {
        return customDistanceToPlayer(builder -> builder.absolute(bounds));
    }

    public static LootItemCondition customDistanceToPlayer(Consumer<DistancePredicateBuilder> action) {
        DistancePredicateBuilder builder = new DistancePredicateBuilder();
        action.accept(builder);
        return new MatchKillerDistance(builder.build());
    }

    public static LootItemCondition playerPredicate(Predicate<ServerPlayer> predicate) {
        return new PlayerParamPredicate(predicate);
    }

    public static LootItemCondition entityPredicate(Predicate<Entity> predicate) {
        return new CustomParamPredicate<>(LootContextParams.THIS_ENTITY, predicate);
    }

    public static LootItemCondition killerPredicate(Predicate<Entity> predicate) {
        return new CustomParamPredicate<>(LootContextParams.KILLER_ENTITY, predicate);
    }

    public static LootItemCondition directKillerPredicate(Predicate<Entity> predicate) {
        return new CustomParamPredicate<>(LootContextParams.DIRECT_KILLER_ENTITY, predicate);
    }

    public static LootItemCondition blockEntityPredicate(Predicate<BlockEntity> predicate) {
        return new CustomParamPredicate<>(LootContextParams.BLOCK_ENTITY, predicate);
    }

    public static LootItemCondition hasAnyStage(String... stages) {
        if (stages.length == 1) {
            String stage = stages[0];
            return new PlayerParamPredicate((player) -> Stages.get(player).has(stage));
        }

        return new PlayerParamPredicate((player) -> {
            for (String stage : stages) {
                if (Stages.get(player).has(stage)) {
                    return true;
                }
            }
            return false;
        });
    }

    public static LootItemCondition fromJson(JsonObject json) {
        return LootJS.CONDITION_GSON.fromJson(json, LootItemCondition.class);
    }

    public static LootItemCondition or(LootItemCondition... conditions) {
        return new AnyOfCondition(conditions);
    }

    public static LootItemCondition and(LootItemCondition... conditions) {
        return new AllOfCondition(conditions);
    }
}