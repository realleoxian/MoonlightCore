package de.realleoxian.moonlightcore.api.transfer.fluid;

import de.realleoxian.moonlightcore.api.fluid.FluidConstants;
import de.realleoxian.moonlightcore.api.transfer.context.ItemAccessContext;
import de.realleoxian.moonlightcore.api.transfer.item.ItemResource;
import de.realleoxian.moonlightcore.api.transfer.storage.ContainerItemContextStorage;
import de.realleoxian.moonlightcore.impl.transfer.StoragePreconditions;
import de.realleoxian.moonlightcore.mixin.BucketItemAccessor;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Items;

public final class BucketStorage extends ContainerItemContextStorage<FluidResource> {

    public BucketStorage(ItemAccessContext context) {
        super(context, 1);
    }

    @Override
    protected ItemResource update(ItemResource current, int index, FluidResource newResource, int newAmount) {
        if (newAmount == 0) {
            return ItemResource.of(Items.BUCKET);
        } else if (newAmount != FluidConstants.BUCKET) {
            return ItemResource.blank();
        }

        return ItemResource.of(newResource.get().getBucket());
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource current, int index) {
        if (current.get() instanceof BucketItem bucketItem) {
            return FluidResource.of(((BucketItemAccessor) bucketItem).getContent());
        }

        return FluidResource.blank();
    }

    @Override
    protected int getAmountFrom(ItemResource current, int index) {
        FluidResource resource = getResourceFrom(current, index);
        return resource.isBlank() ? 0 : FluidConstants.BUCKET;
    }

    @Override
    public int getCapacity(int index, FluidResource resource) {
        StoragePreconditions.singleSlotIndexCheck(index);
        return FluidConstants.BUCKET;
    }

}
