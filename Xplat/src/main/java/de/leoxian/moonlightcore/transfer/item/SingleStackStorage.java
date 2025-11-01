package de.leoxian.moonlightcore.transfer.item;

import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

public abstract class SingleStackStorage extends SnapshotJournal<ItemStack> implements SingleSlotStorage<ItemResource> {

    public abstract ItemStack getStack();

    public abstract void setStack(ItemStack stack);

    @Override
    public int insert(Transaction tx, ItemResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        ItemStack currentStack = getStack();

        if(isResourceValid(resource) && canInsert(resource)) {
            int insertedAmount = Math.min(amount, getCapacity(resource) - currentStack.getCount());

            if(insertedAmount > 0) {
                updateSnapshots(tx);
                currentStack = getStack();

                if(currentStack.isEmpty()) {
                    currentStack = resource.toStack(insertedAmount);
                } else {
                    currentStack.grow(insertedAmount);
                }

                setStack(currentStack);
                return insertedAmount;
            }
        }

        return 0;
    }

    @Override
    public int extract(Transaction tx, ItemResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        ItemStack currentStack = getStack();

        if(resource.is(currentStack.getItem()) && canExtract(resource)) {
            int extracted = Math.min(currentStack.getCount(), amount);

            if(extracted > 0) {
                this.updateSnapshots(tx);
                currentStack = getStack();
                currentStack.shrink(extracted);
                setStack(currentStack);

                return extracted;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceValid(ItemResource resource) {
        return resource.isEmpty() || resource.fullyMatches(getStack().getItem(), getStack().getTag());
    }

    @Override
    public int getCapacity(ItemResource resource) {
        return resource.get().getMaxStackSize();
    }

    @Override
    public ItemResource resource() {
        return ItemResource.of(this.getStack().getItem(), this.getStack().getTag());
    }

    @Override
    public int amount() {
        return this.getStack().getCount();
    }

    @Override
    public ItemStack createSnapshot() {
        ItemStack original = getStack();
        setStack(original.copy());

        return original;
    }

    @Override
    public void revertToSnapshot(ItemStack snapshot) {
        setStack(snapshot);
    }

    protected boolean canInsert(ItemResource resource) {
        return true;
    }

    protected boolean canExtract(ItemResource resource) {
        return true;
    }
}
