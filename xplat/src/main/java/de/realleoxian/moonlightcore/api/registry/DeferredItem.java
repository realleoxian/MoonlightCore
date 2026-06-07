package de.realleoxian.moonlightcore.api.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class DeferredItem<I extends Item> extends DeferredHolder<Item, I> implements ItemLike {
    public DeferredItem(ResourceKey<Item> key) {
        super(key);
    }

    public ItemStack toStack(int count) {
        return new ItemStack((Holder<Item>) this, count);
    }

    public ItemStack toStack() {
        return toStack(1);
    }

    @Override
    public Item asItem() {
        return value();
    }
}
