package com.github.llytho.lootjs.core;

import com.github.llytho.lootjs.util.LootContextUtils;
import com.google.common.base.Strings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LootModificationDebug {

    public static final int BASE_LAYER = 1;
    protected final List<Entry> entries = new ArrayList<>();
    private int layer = BASE_LAYER;


    public static LootModificationDebug context(LootContext context) {
        LootModificationDebug cd = new LootModificationDebug();

        cd.write("LootTable", context.getQueriedLootTableId());

        ILootContextData data = context.getParamOrNull(Constants.DATA);
        if (data != null) {
            cd.write("Loot type", data.getLootContextType());
            cd.write("Current loot :");
            cd.pushLayer();
            for (ItemStack itemStack : data.getGeneratedLoot()) {
                String tag = "";
                if (itemStack.hasTag()) tag += " " + itemStack.getTag();
                cd.write("- " + itemStack + tag);
            }
            cd.popLayer();
        }

        Vector3d origin = context.getParamOrNull(LootParameters.ORIGIN);
        cd.write("Position", origin);
        cd.write("Block", context.getParamOrNull(LootParameters.BLOCK_STATE));
        cd.write("Explosion", context.getParamOrNull(LootParameters.EXPLOSION_RADIUS));
        cd.write("Entity", context.getParamOrNull(LootParameters.THIS_ENTITY));
        cd.write("Killer Entity", context.getParamOrNull(LootParameters.KILLER_ENTITY));
        cd.write("Direct Killer", context.getParamOrNull(LootParameters.DIRECT_KILLER_ENTITY));

        ServerPlayerEntity playerOrNull = LootContextUtils.getPlayerOrNull(context);
        if (playerOrNull != null) {
            cd.write("Player", playerOrNull);
            cd.write("Player Pos", playerOrNull.position());
            if (origin != null) {
                cd.write("Distance", playerOrNull.position().distanceTo(origin));
            }
            cd.write("Mainhand", playerOrNull.getItemBySlot(EquipmentSlotType.MAINHAND));
            cd.write("Offhand", playerOrNull.getItemBySlot(EquipmentSlotType.OFFHAND));
            cd.write("Head Item", playerOrNull.getItemBySlot(EquipmentSlotType.HEAD));
            cd.write("Chest Item", playerOrNull.getItemBySlot(EquipmentSlotType.CHEST));
            cd.write("Legs Item", playerOrNull.getItemBySlot(EquipmentSlotType.LEGS));
            cd.write("Feet Item", playerOrNull.getItemBySlot(EquipmentSlotType.FEET));
        }

        return cd;
    }

    public void pushLayer() {
        layer++;
    }

    public void popLayer() {
        if (layer > BASE_LAYER) {
            layer--;
        }
    }

    public void write(String string) {
        entries.add(new Entry(layer, string));
    }

    public void write(boolean succeed, String string) {
        entries.add(new TestedEntry(layer, succeed, string));
    }

    public <T> void write(String prefix, @Nullable T t) {
        if (t != null) {
            write(prefix + Strings.repeat(" ", 13 - prefix.length()) + ": " + t);
        }
    }

    public void reset() {
        entries.clear();
        layer = BASE_LAYER;
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public static class Entry {
        protected final int layer;
        protected final String text;

        protected Entry(int layer, String text) {
            this.layer = layer;
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public int getLayer() {
            return layer;
        }
    }

    public static class TestedEntry extends Entry {
        protected final boolean succeed;

        protected TestedEntry(int layer, boolean succeed, String text) {
            super(layer, text);
            this.succeed = succeed;
        }

        @Override
        public String getText() {
            return (succeed ? "\uD83D\uDFE2" : "\uD83D\uDD34") + super.getText();
        }
    }
}