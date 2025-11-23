package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public abstract class SingleStackStorage extends SnapshotJournal<ItemStack> implements SingleSlotStorage<ItemResource> {

    public abstract void setStack(ItemStack stack);

    public abstract ItemStack getStack();

    @Override
    public int insert(TransactionContext context, ItemResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
        ItemStack currentStack = getStack();

        if((insertedResource.matches(currentStack) || currentStack.isEmpty()) && canInsert(insertedResource)) {
            int inserted = Math.min(maxAmount, getCapacity(insertedResource) - currentStack.getCount());

            if(inserted > 0) {
                updateSnapshots(context);
                currentStack = getStack();

                if(currentStack.isEmpty()) {
                    currentStack = insertedResource.toStack(inserted);
                } else {
                    currentStack.grow(inserted);
                }
                setStack(currentStack);

                return inserted;
            }
        }

        return 0;
    }

    protected boolean canInsert(ItemResource resource) {
        return true;
    }

    @Override
    public int extract(TransactionContext context, ItemResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
        ItemStack currentStack = getStack();

        if(extractedResource.matches(currentStack) && canExtract(extractedResource)) {
            int extracted = Math.min(currentStack.getCount(), maxAmount);

            if(extracted > 0) {
                this.updateSnapshots(context);
                currentStack = getStack();
                currentStack.shrink(extracted);
                setStack(currentStack);

                return extracted;
            }
        }

        return 0;
    }

    protected boolean canExtract(ItemResource resource) {
        return true;
    }

    @Override
    public int getCapacity(ItemResource resource) {
        return resource.getResource().getMaxStackSize();
    }

    @Override
    public boolean isResourceBlank() {
        return getStack().isEmpty();
    }

    @Override
    public ItemResource getResource() {
        return ItemResource.of(getStack());
    }

    @Override
    public int getAmount() {
        return getStack().getCount();
    }

    @Override
    public ItemStack createSnapshot() {
        ItemStack original = getStack();
        setStack(original.copy());

        return original;
    }

    @Override
    public void revertToSnapshot(ItemStack snapshot) {
        this.setStack(snapshot);
    }

    @Override
    public String toString() {
        return "SingleStackStorage[" + getStack() + "]";
    }
}
