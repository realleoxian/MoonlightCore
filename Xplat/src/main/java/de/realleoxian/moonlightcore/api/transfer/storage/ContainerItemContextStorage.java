package de.realleoxian.moonlightcore.api.transfer.storage;

import de.realleoxian.moonlightcore.api.transfer.Resource;
import de.realleoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.realleoxian.moonlightcore.api.transfer.item.ItemResource;
import de.realleoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.realleoxian.moonlightcore.impl.transfer.StoragePreconditions;

import java.util.Objects;

public abstract class ContainerItemContextStorage<T extends Resource<?>> implements Storage<T> {
    protected final ItemAccessContext context;
    protected final int size;

    protected ContainerItemContextStorage(ItemAccessContext context, int size) {
        this.context = context;
        this.size = size;
    }

    protected abstract ItemResource update(ItemResource current, int index, T newResource, int newAmount);

    protected abstract T getResourceFrom(ItemResource current, int index);

    protected abstract int getAmountFrom(ItemResource current, int index);

    @Override
    public int insert(TransactionContext tx, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, size());
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        int contextAmount = context.getAmount();
        if (contextAmount == 0)
            return 0;

        int amountPerItem = maxAmount / contextAmount;
        ItemResource contextResource = context.getResource();
        int currentAAmountPerItem = getAmountFrom(contextResource, index);

        if((currentAAmountPerItem == 0 || resource == getResourceFrom(contextResource, index)) && canInsert(index, resource) && supportsInsertion()) {
            int insertedPerItem = Math.min(amountPerItem, getCapacity(index, resource) - currentAAmountPerItem);

            if(insertedPerItem > 0) {
                ItemResource filledResource = update(contextResource, index, resource, insertedPerItem + currentAAmountPerItem);

                if(!filledResource.isBlank()) {
                    return insertedPerItem * context.exchange(tx, filledResource, contextAmount);
                }
            }
        }

        return 0;
    }

    @Override
    public int extract(TransactionContext tx, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, size());
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        int contextAmount = context.getAmount();
        if (contextAmount == 0)
            return 0;

        ItemResource contextResource = context.getResource();
        T currentResource = getResourceFrom(contextResource, index);

        if(resource == currentResource) {
            int currentAmountPerItem = getAmountFrom(contextResource, index);
            int extractedPerItem = Math.min(maxAmount / contextAmount, currentAmountPerItem);

            if (extractedPerItem > 0) {
                ItemResource emptiedResource = update(contextResource, index, resource, currentAmountPerItem - extractedPerItem);

                if (!emptiedResource.isBlank()) {
                    return extractedPerItem * context.exchange(tx, emptiedResource, contextAmount);
                }
            }
        }

        return 0;
    }

    @Override
    public T getResource(int index) {
        Objects.checkIndex(index, size());
        return getResourceFrom(context.getResource(), index);
    }

    @Override
    public int getAmount(int index) {
        Objects.checkIndex(index, size());
        return getAmountFrom(context.getResource(), index);
    }

    @Override
    public boolean isBlank(int index) {
        Objects.checkIndex(index, size());
        return getResourceFrom(context.getResource(), index).isBlank();
    }

    @Override
    public int size() {
        return size;
    }
}
