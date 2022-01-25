package com.github.llytho.lootjs_test.tests;

import com.github.llytho.lootjs.kube.ConditionsContainer;
import com.github.llytho.lootjs.loot.condition.AnyBiomeCheck;
import com.github.llytho.lootjs.loot.condition.AnyStructure;
import com.github.llytho.lootjs.loot.condition.BiomeCheck;
import com.github.llytho.lootjs_test.AllTests;
import com.github.llytho.lootjs_test.TestHelper;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.common.BiomeDictionary;

import java.util.HashMap;
import java.util.List;

public class ConditionsContainerTests {
    private static final TestConditionsContainer conditions = new TestConditionsContainer();

    public static void loadTests() {


        AllTests.add("ConditionsContainer", helper -> {
            helper.debugStack.pushLayer();

            biome(helper);
            anyBiome(helper);
            anyStructure(helper);
            entityTargets(helper);
        });

    }

    private static void entityTargets(TestHelper helper) {
        helper.debugStack.h2("entity targets");
        conditions.matchEntity(e -> {});
        helper.shouldSucceed(
                conditions.<EntityHasProperty>last().entityTarget == LootContext.EntityTarget.THIS,
                "EntityTarget is = " + LootContext.EntityTarget.THIS.name());

        conditions.matchKiller(e -> {});
        helper.shouldSucceed(
                conditions.<EntityHasProperty>last().entityTarget == LootContext.EntityTarget.KILLER,
                "EntityTarget is = " + LootContext.EntityTarget.KILLER.name());

        conditions.matchDirectKiller(e -> {});
        helper.shouldSucceed(conditions.<EntityHasProperty>last().entityTarget ==
                             LootContext.EntityTarget.DIRECT_KILLER,
                "EntityTarget is = " + LootContext.EntityTarget.DIRECT_KILLER.name());
    }

    private static void anyStructure(TestHelper helper) {
        helper.debugStack.h2("anyStructure");
        conditions.anyStructure(new ResourceLocation[]{
                new ResourceLocation("stronghold"), new ResourceLocation("village")
        }, true);
        helper.shouldSucceed(conditions.last instanceof AnyStructure, "AnyStructure instance");
        helper.shouldSucceed(conditions.<AnyStructure>last().getStructures().length == 2, "Should have 2 structures");
        helper.shouldThrow(() -> conditions.anyStructure(new ResourceLocation[]{
                new ResourceLocation("wrong_structure")
        }, true), IllegalStateException.class, "'wrong_structure' does not exist");
    }

    private static void anyBiome(TestHelper helper) {
        helper.debugStack.h2("anyBiome");
        conditions.anyBiome("minecraft:desert", "#nether", "minecraft:jungle");
        helper.shouldSucceed(conditions.last instanceof AnyBiomeCheck, "AnyBiomeCheck instance");
        helper.shouldSucceed(conditions.<AnyBiomeCheck>last().getBiomes().size() == 2, "Should have 2 biomes");
        helper.shouldSucceed(conditions.<AnyBiomeCheck>last().getTypes().size() == 1, "Should have 1 type");
        helper.shouldThrow(() -> conditions.anyBiome("not_existing_biome"),
                IllegalStateException.class,
                "'not_existing_biome' does not exist");
        helper.shouldThrow(() -> conditions.anyBiome("#wrong_type"),
                IllegalStateException.class,
                "'#wrong_type' does not exist");
    }

    private static void biome(TestHelper helper) {
        helper.debugStack.h2("biome");
        conditions.biome("minecraft:desert", "#nether", "minecraft:jungle");
        helper.shouldSucceed(conditions.last instanceof BiomeCheck, "BiomeCheck instance");
        helper.shouldSucceed(conditions.<BiomeCheck>last().getBiomes().size() == 2, "Should have 2 biomes");
        helper.shouldSucceed(conditions.<BiomeCheck>last().getTypes().size() == 1, "Should have 1 type");
        helper.shouldThrow(() -> conditions.biome("not_existing_biome"),
                IllegalStateException.class,
                "'not_existing_biome' does not exist");
        helper.shouldThrow(() -> conditions.biome("#wrong_type"),
                IllegalStateException.class,
                "'#wrong_type' does not exist");
    }

    public static class TestConditionsContainer implements ConditionsContainer<TestConditionsContainer> {

        private ILootCondition last;

        @Override
        public TestConditionsContainer addCondition(ILootCondition pCondition) {
            this.last = pCondition;
            return this;
        }

        public <T extends ILootCondition> T last() {
            //noinspection unchecked
            return (T) last;
        }
    }
}
