package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.item.Item;

public abstract class SingleItemResourceStorage<T extends TransferResource<?>> implements SingleSlotStorage<T> {

    private final ContainerItemContext context;
    private final Item item;

    public SingleItemResourceStorage(ContainerItemContext context) {
        this.context = context;
        this.item = context.getResource().getResource();
    }

    protected abstract ItemResource getUpdatedResource(ItemResource currentResource, T newResource, int newAmount);

    protected abstract T getResource(ItemResource currentResource);

    protected abstract int getAmount(ItemResource currentResource);

    public abstract int getCapacity(T resource);

    protected abstract T getBlankResource();

    protected boolean canInsert(T resource) {
        return true;
    }

    protected boolean canExtract(T resource) {
        return true;
    }

    private boolean tryUpdateStorage(TransactionContext context, T newResource, int newAmount) {
        return this.context.exchange(context, getUpdatedResource(this.context.getResource(), newResource, newAmount), 1) == 1;
    }

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);

        if(!canInsert(insertedResource) || !this.context.getResource().isOf(this.item)) {
            return 0;
        }

        int amount = getAmount(this.context.getResource());
        T resource = getResource(this.context.getResource());
        int inserted = 0;

        if(resource.isBlank() || amount == 0) {
            inserted = Math.min(getCapacity(insertedResource), maxAmount);
        } else if (resource.equals(insertedResource)) {
            inserted = Math.min(getCapacity(insertedResource) - amount, maxAmount);
        }

        if(inserted > 0) {
            if(tryUpdateStorage(context, insertedResource, amount + inserted)) {
                return inserted;
            }
        }

        return 0;


    }

    @Override
    public boolean supportsInsertion() {
        return this.context.getResource().isOf(this.item);
    }

    @Override
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
        if(!canExtract(extractedResource) || !this.context.getResource().isOf(this.item)) {
            return 0;
        }

        int amount = getAmount(this.context.getResource());
        T resource = getResource(this.context.getResource());
        int extracted = 0;

        if(resource.equals(extractedResource)) {
            extracted = Math.min(maxAmount, amount);
        }

        if(extracted > 0) {
            if(tryUpdateStorage(context, resource, amount - extracted)) {
                return extracted;
            }
        }

        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return this.context.getResource().isOf(this.item);
    }

    @Override
    public boolean isResourceBlank() {
        return this.getResource().isBlank();
    }

    @Override
    public T getResource() {
        if(this.context.getResource().isOf(this.item)) {
            return getResource(this.context.getResource());
        }

        return getBlankResource();
    }

    @Override
    public int getAmount() {
        if(this.context.getResource().isOf(this.item)) {
            return getAmount(this.context.getResource());
        }

        return 0;
    }

    public int getCapacity() {
        if(this.context.getResource().isOf(this.item)) {
            return getCapacity(this.getResource());
        }

        return 0;
    }

    @Override
    public String toString() {
        return "SingleItemResourceStorage[context=" + context + "/" + item + "]";
    }

}
