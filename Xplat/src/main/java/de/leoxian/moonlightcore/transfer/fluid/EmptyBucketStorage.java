package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.mixin.accessor.BucketItemAccessor;
import de.leoxian.moonlightcore.transfer.EmptyStorageView;
import de.leoxian.moonlightcore.transfer.InsertionOnlyStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.StorageView;
import de.leoxian.moonlightcore.transfer.context.ItemStorageContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class EmptyBucketStorage implements InsertionOnlyStorage<FluidResource> {
    private final List<StorageView<FluidResource>> blankView = List.of(new EmptyStorageView<>(FluidResource.empty(), FluidConstants.BUCKET));
    private final ItemStorageContext context;

    public EmptyBucketStorage(ItemStorageContext context) {
        this.context = context;
    }

    @Override
    public int insert(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);
        if(!context.resource().is(Items.BUCKET)) return 0;

        Item fullBucket = resource.get().getBucket();
        if(fullBucket instanceof BucketItemAccessor accessor && resource.is(accessor.getContent())) {
            if(amount >= FluidConstants.BUCKET) {
                ItemResource newResource = ItemResource.of(fullBucket, context.resource().getNBT());

                if(context.exchange(tx, newResource, 1) == 1) {
                    return FluidConstants.BUCKET;
                }
            }
        }

        return 0;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull StorageView<FluidResource> get(int index) {
        return this.blankView.get(0);
    }

    @Override
    public @NotNull Iterator<StorageView<FluidResource>> iterator() {
        return this.blankView.iterator();
    }
}
