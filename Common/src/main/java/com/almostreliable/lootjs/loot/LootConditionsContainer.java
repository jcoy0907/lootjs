package com.almostreliable.lootjs.loot;

import com.almostreliable.lootjs.core.filters.ItemFilter;
import com.almostreliable.lootjs.core.filters.Resolver;
import com.almostreliable.lootjs.loot.condition.PlayerParamPredicate;
import com.almostreliable.lootjs.loot.condition.builder.DamageSourcePredicateBuilder;
import com.almostreliable.lootjs.loot.condition.builder.DistancePredicateBuilder;
import com.almostreliable.lootjs.loot.condition.builder.EntityPredicateBuilder;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings({ "UnusedReturnValue", "unused" })
public interface LootConditionsContainer<B extends LootConditionsContainer<?>> {

    default B matchMainHand(ItemFilter filter) {
        return addCondition(LootCondition.matchMainHand(filter));
    }

    default B matchOffHand(ItemFilter filter) {
        return addCondition(LootCondition.matchOffHand(filter));
    }

    default B matchEquip(EquipmentSlot slot, ItemFilter filter) {
        return addCondition(LootCondition.matchEquip(slot, filter));
    }

    default B survivesExplosion() {
        return addCondition(LootCondition.survivesExplosion());
    }

    default B timeCheck(long period, int min, int max) {
        return addCondition(LootCondition.timeCheck(period, min, max));
    }

    default B timeCheck(int min, int max) {
        return addCondition(LootCondition.timeCheck(min, max));
    }

    default B weatherCheck(Map<String, Boolean> map) {
        return addCondition(LootCondition.weatherCheck(map));
    }

    default B randomChance(float value) {
        return addCondition(LootCondition.randomChance(value));
    }

    default B randomChanceWithLooting(float value, float looting) {
        return addCondition(LootCondition.randomChanceWithLooting(value, looting));
    }

    default B randomChanceWithEnchantment(@Nullable Enchantment enchantment, float[] chances) {
        return addCondition(LootCondition.randomChanceWithEnchantment(enchantment, chances));
    }

    default B biome(Resolver... resolvers) {
        return addCondition(LootCondition.biome(resolvers));
    }

    default B anyBiome(Resolver... resolvers) {
        return addCondition(LootCondition.anyBiome(resolvers));
    }

    default B anyDimension(ResourceLocation... dimensions) {
        return addCondition(LootCondition.anyDimension(dimensions));
    }

    default B anyStructure(String[] idOrTags, boolean exact) {
        return addCondition(LootCondition.anyStructure(idOrTags, exact));
    }

    default B lightLevel(int min, int max) {
        return addCondition(LootCondition.lightLevel(min, max));
    }

    default B killedByPlayer() {
        return addCondition(LootCondition.killedByPlayer());
    }

    default B matchBlockState(Block block, Map<String, String> propertyMap) {
        return addCondition(LootCondition.matchBlockState(block, propertyMap));
    }

    default B matchFluid(Resolver resolver) {
        throw new UnsupportedOperationException("Not implemented currently.");
    }

    default B matchEntity(Consumer<EntityPredicateBuilder> action) {
        return addCondition(LootCondition.matchEntity(action));
    }

    default B matchKiller(Consumer<EntityPredicateBuilder> action) {
        return addCondition(LootCondition.matchKiller(action));
    }

    default B matchDirectKiller(Consumer<EntityPredicateBuilder> action) {
        return addCondition(LootCondition.matchDirectKiller(action));
    }

    default B matchPlayer(Consumer<EntityPredicateBuilder> action) {
        return addCondition(LootCondition.matchPlayer(action));
    }

    default B matchDamageSource(Consumer<DamageSourcePredicateBuilder> action) {
        return addCondition(LootCondition.matchDamageSource(action));
    }

    default B distanceToKiller(MinMaxBounds.Doubles bounds) {
        return addCondition(LootCondition.distanceToKiller(bounds));
    }

    default B customDistanceToPlayer(Consumer<DistancePredicateBuilder> action) {
        return addCondition(LootCondition.customDistanceToPlayer(action));
    }

    default B playerPredicate(Predicate<ServerPlayer> predicate) {
        return addCondition(new PlayerParamPredicate(predicate));
    }

    default B entityPredicate(Predicate<Entity> predicate) {
        return addCondition(LootCondition.entityPredicate(predicate));
    }

    default B killerPredicate(Predicate<Entity> predicate) {
        return addCondition(LootCondition.killerPredicate(predicate));
    }

    default B directKillerPredicate(Predicate<Entity> predicate) {
        return addCondition(LootCondition.directKillerPredicate(predicate));
    }

    default B blockEntityPredicate(Predicate<BlockEntity> predicate) {
        return addCondition(LootCondition.blockEntityPredicate(predicate));
    }

    default B hasAnyStage(String... stages) {
        return addCondition(LootCondition.hasAnyStage(stages));
    }

    default B not(Consumer<LootConditionsContainer<B>> action) {
        // TODO rework, use vanilla but keep tracking
        List<LootItemCondition> conditions = createConditions(action);
        if (conditions.size() != 1) {
            throw new IllegalArgumentException("You only can have one condition for `not`");
        }

        return addCondition(new InvertedLootItemCondition(conditions.get(0)));
    }

    default B or(Consumer<LootConditionsContainer<B>> action) {
        List<LootItemCondition> conditions = createConditions(action);
        LootItemCondition[] array = conditions.toArray(new LootItemCondition[0]);
        return addCondition(LootCondition.or(array));
    }

    default B and(Consumer<LootConditionsContainer<B>> action) {
        List<LootItemCondition> conditions = createConditions(action);
        LootItemCondition[] array = conditions.toArray(new LootItemCondition[0]);
        return addCondition(LootCondition.and(array));
    }

    default List<LootItemCondition> createConditions(Consumer<LootConditionsContainer<B>> action) {
        List<LootItemCondition> conditions = new ArrayList<>();
        LootConditionsContainer<B> container = new LootConditionsContainer<B>() {
            @Override
            public B addCondition(LootItemCondition condition) {
                conditions.add(condition);
                //noinspection unchecked
                return (B) this;
            }
        };
        action.accept(container);
        return conditions;
    }

    default B customCondition(JsonObject json) {
        return addCondition(LootCondition.fromJson(json));
    }

    B addCondition(LootItemCondition condition);
}