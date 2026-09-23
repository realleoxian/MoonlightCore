package de.leoxian.moonlightcore.common.transfer.storage;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.Resource;
import de.leoxian.moonlightcore.common.transfer.resource.ResourceStack;
import net.minecraft.core.NonNullList;

public abstract class ResourceStacksStorage<T extends Resource> extends StacksStorage<T, ResourceStack<T>> {
	public ResourceStacksStorage(Codec<T> resourceCodec, T emptyResource, NonNullList<ResourceStack<T>> stacks) {
		super(ResourceStack.codec(resourceCodec), new ResourceStack<>(emptyResource, 0), stacks);
	}

	public ResourceStacksStorage(Codec<T> resourceCodec, T emptyResource, int size) {
		super(ResourceStack.codec(resourceCodec), new ResourceStack<>(emptyResource, 0), size);
	}

	@Override
	protected ResourceStack<T> createStack(T resource, int amount) {
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
	protected ResourceStack<T> copyStack(ResourceStack<T> stack) {
		return new ResourceStack<>(stack.resource(), stack.amount());
	}
}
