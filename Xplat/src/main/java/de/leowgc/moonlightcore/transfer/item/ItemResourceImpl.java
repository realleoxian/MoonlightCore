package de.leowgc.moonlightcore.transfer.item;

import de.leowgc.moonlightcore.api.transfer.item.ItemResource;
import net.minecraft.world.item.ItemStack;

public final class ItemResourceImpl implements ItemResource {
    private ItemStack stack;
    private int amount;

    public ItemResourceImpl(ItemStack stack, int amount) {
        this.stack = stack.copy();
        this.amount = Math.min(amount, stack.getMaxStackSize());
    }

    @Override
    public ItemStack get() {
        return this.stack.copy();
    }

    @Override
    public int amount() {
        return this.amount;
    }

    @Override
    public ItemResource copy() {
        return new ItemResourceImpl(this.stack, this.amount);
    }

    @Override
    public boolean isBlank() {
        return this.stack.isEmpty() || this.amount <= 0;
    }
}
