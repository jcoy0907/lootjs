package com.github.llytho.lootjs.kube.predicate;

import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.NBTPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class BlockPredicateJS extends BlockPredicate {
    private final dev.latvian.kubejs.block.predicate.BlockPredicate kubeBlockPredicate;

    public BlockPredicateJS(dev.latvian.kubejs.block.predicate.BlockPredicate pKubeBlockPredicate) {
        // As we override `matches` we just null this and set defaults. We don't want them! >:(
        super(null, null, StatePropertiesPredicate.ANY, NBTPredicate.ANY);
        kubeBlockPredicate = pKubeBlockPredicate;
    }

    @Override
    public boolean matches(ServerWorld pLevel, BlockPos pBlockPos) {
        return kubeBlockPredicate.check(new BlockContainerJS(pLevel, pBlockPos));
    }
}