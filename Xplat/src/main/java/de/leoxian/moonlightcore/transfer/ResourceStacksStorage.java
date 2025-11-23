package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;

public abstract class ResourceStacksStorage<T> extends StacksStorage<T, ResourceStack<T>> {
    protected ResourceStacksStorage(int size, ResourceStack<T> emptyStack, Codec<ResourceStack<T>> stackCodec) {
        super(size, emptyStack, stackCodec);
    }

    protected ResourceStacksStorage(NonNullList<ResourceStack<T>> stacks, ResourceStack<T> emptyStack, Codec<ResourceStack<T>> stackCodec) {
        super(stacks, emptyStack, stackCodec);
    }

    @Override
    protected T getResourceFrom(ResourceStack<T> stack) {
        return stack.resource();
    }

    @Override
    protected ResourceStack<T> getStackFrom(T resource, int amount) {
        return new ResourceStack<>(resource, amount);
    }

    @Override
    protected ResourceStack<T> copyOf(ResourceStack<T> stack) {
        return stack;
    }

    @Override
    protected int getAmountFrom(ResourceStack<T> stack) {
        return stack.amount();
    }
}
