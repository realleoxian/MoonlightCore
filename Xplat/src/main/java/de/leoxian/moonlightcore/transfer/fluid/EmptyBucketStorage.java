package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.mixin.accessor.BucketItemAccessor;
import de.leoxian.moonlightcore.transfer.BlankResourceView;
import de.leoxian.moonlightcore.transfer.InsertionOnlyStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.StorageView;
import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Iterator;
import java.util.List;

public class EmptyBucketStorage implements InsertionOnlyStorage<FluidResource> {
    private final List<StorageView<FluidResource>> blankView = List.of(new BlankResourceView<>(FluidResource.blank(), FluidConstants.BUCKET));
    private final ContainerItemContext context;

    public EmptyBucketStorage(ContainerItemContext context) {
        this.context = context;
    }

    @Override
    public int insert(TransactionContext context, FluidResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
        if(!this.context.getResource().isOf(Items.BUCKET)) return 0;

        Item fullBucket = insertedResource.getResource().getBucket();
        if(fullBucket instanceof BucketItemAccessor accessor && insertedResource.isOf(accessor.getContent())) {
            if(maxAmount >= FluidConstants.BUCKET) {
                ItemResource newResource = ItemResource.of(fullBucket, this.context.getResource().getNBT());

                if(this.context.exchange(context, newResource, 1) == 1) {
                    return FluidConstants.BUCKET;
                }
            }
        }

        return 0;
    }

    @Override
    public StorageView<FluidResource> get(int index) {
        if(index != 0) {
            throw new IndexOutOfBoundsException("Slot " + index + " does not exist in empty bucket storage");
        }

        return blankView.get(0);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @Nonnull Iterator<StorageView<FluidResource>> iterator() {
        return blankView.iterator();
    }

    @Override
    public String toString() {
        return "EmptyBucketStorage[" + this.context + "]";
    }
}
