package de.realleoxian.moonlightcore.api.transfer.storage;

import com.mojang.serialization.Codec;
import de.realleoxian.moonlightcore.api.transfer.ResourceStack;
import net.minecraft.core.NonNullList;

public abstract class ResourceStackStorage<T> extends StacksStorage<T, ResourceStack<T>> {

    public ResourceStackStorage(int size, ResourceStack<T> emptyStack, Codec<T> codec) {
        super(size, emptyStack, ResourceStack.codec(codec));
    }

    public ResourceStackStorage(NonNullList<ResourceStack<T>> stacks, ResourceStack<T> emptyStack, Codec<T> codec) {
        super(stacks, emptyStack, ResourceStack.codec(codec));
    }

    @Override
    protected ResourceStack<T> copyStack(ResourceStack<T> stack) {
        return stack;
    }

    @Override
    protected ResourceStack<T> getStackFrom(T resource, int amount) {
        return new ResourceStack<>(resource, amount);
    }

    @Override
    protected T getResourceFrom(ResourceStack<T> stack) {
        return stack.resource();
    }

    @Override
    protected int getAmountFrom(ResourceStack<T> stack) {
        return stack.amount();
    }

    @Override
    protected boolean isEmptyStack(ResourceStack<T> stack) {
        return stack.amount() == 0;
    }

}
