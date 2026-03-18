package de.leoxian.moonlightcore.api.transfer.item;

import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.api.transfer.transaction.SnapshotJournal;
import de.leoxian.moonlightcore.api.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.impl.transfer.StoragePreconditions;
import net.minecraft.world.item.ItemStack;

public abstract class SingleStackStorage extends SnapshotJournal<ItemStack> implements Storage<ItemResource> {

    public abstract void setStack(ItemStack stack);

    public abstract ItemStack getStack();

    @Override
    public int insert(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        StoragePreconditions.singleSlotIndexCheck(index);
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);

        ItemStack currentStack = getStack();
        if((resource.matches(currentStack) || currentStack.isEmpty()) && canInsert(resource) && supportsInsertion()) {
            int inserted = Math.min(maxAmount, getCapacity(index, resource) - currentStack.getCount());

            if(inserted > 0) {
                updateSnapshots(tx);

                currentStack = getStack();
                if (currentStack.isEmpty()) currentStack = resource.toStack(inserted);
                else currentStack.grow(inserted);
                setStack(currentStack);

                return inserted;
            }
        }
        return 0;
    }

    @Override
    public int extract(TransactionContext tx, int index, ItemResource resource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        ItemStack currentStack = getStack();

        if (resource.matches(currentStack) && canExtract(resource) && supportsExtraction()) {
            int extracted = Math.min(currentStack.getCount(), maxAmount);

            if (extracted > 0) {
                updateSnapshots(tx);
                currentStack = getStack();
                currentStack.shrink(extracted);
                setStack(currentStack);

                return extracted;
            }
        }
        return 0;
    }

    @Override
    public int getCapacity(int index, ItemResource resource) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return resource.get().getMaxStackSize();
    }

    @Override
    public ItemResource getResource(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return ItemResource.fromItemStack(getStack());
    }

    @Override
    public int getAmount(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return getStack().getCount();
    }

    @Override
    public boolean isBlank(int index) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return getStack().isEmpty();
    }

    @Override
    public int size() {
        return 1;
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
}
