package de.leoxian.moonlightcore.transfer;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;

public abstract class ResourceStacksStorage<V, R extends TransferResource<V>> extends StacksStorage<V, R, ResourceStack<V, R>> {
    protected ResourceStacksStorage(int size, R emptyResource, Codec<R> resourceCodec) {
        super(size, new ResourceStack<>(emptyResource, 0), ResourceStack.codec(resourceCodec));
    }

    protected ResourceStacksStorage(NonNullList<ResourceStack<V, R>> stacks, R emptyResource, Codec<R> resourceCodec) {
        super(stacks, new ResourceStack<>(emptyResource, 0), ResourceStack.codec(resourceCodec));
    }

    @Override
    protected R getResourceFrom(ResourceStack<V, R> stack) {
        return stack.resource();
    }

    @Override
    protected int getAmountFrom(ResourceStack<V, R> stack) {
        return stack.amount();
    }

    @Override
    protected ResourceStack<V, R> getStackFrom(R resource, int amount) {
        return new ResourceStack<>(resource, amount);
    }

    @Override
    protected ResourceStack<V, R> copyOf(ResourceStack<V, R> stack) {
        return stack;
    }
}
