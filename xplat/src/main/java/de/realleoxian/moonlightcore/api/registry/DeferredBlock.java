package de.realleoxian.moonlightcore.api.registry;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class DeferredBlock<B extends Block> extends DeferredHolder<Block, B> implements ItemLike {
    public DeferredBlock(ResourceKey<Block> key) {
        super(key);
    }

    public BlockState defaultBlockState() {
        return value().defaultBlockState();
    }

    public ItemStack toStack(int count) {
        return new ItemStack(this, count);
    }

    public ItemStack toStack() {
        return toStack(1);
    }

    @Override
    public Item asItem() {
        return value().asItem();
    }
}
