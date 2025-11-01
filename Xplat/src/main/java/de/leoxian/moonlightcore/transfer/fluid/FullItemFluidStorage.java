package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.ExtractionOnlyStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.context.ItemStorageContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class FullItemFluidStorage implements ExtractionOnlyStorage<FluidResource>, SingleSlotStorage<FluidResource> {

    private final ItemStorageContext context;
    private final Function<ItemResource, ItemResource> fullToEmptyMapping;
    private final FluidResource containedFluid;
    private final Item fullItem;
    private final int containedAmount;

    public FullItemFluidStorage(ItemStorageContext context, Item emptyResource, FluidResource containedFluid, int containedAmount) {
        this(context, fullResource -> ItemResource.of(emptyResource, fullResource.getNBT()), containedFluid, containedAmount);
    }

    public FullItemFluidStorage(ItemStorageContext context, Function<ItemResource, ItemResource> fullToEmptyMapping, FluidResource containedFluid, int containedAmount) {
        StorageInternals.checkNonEmptyNonNegative(containedFluid, containedAmount);

        this.context = context;
        this.fullToEmptyMapping = fullToEmptyMapping;
        this.containedFluid = containedFluid;
        this.fullItem = context.resource().get();
        this.containedAmount = containedAmount;
    }

    @Override
    public int extract(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(!this.context.resource().is(this.fullItem)) {
            return 0;
        }

        if(resource.is(this.containedFluid.get()) && amount == this.containedAmount) {
            ItemResource newResource = this.fullToEmptyMapping.apply(this.context.resource());

            if(this.context.exchange(tx, newResource, 1) == 1) {
                return containedAmount;
            }
        }

        return 0;
    }

    @Override
    public boolean isResourceValid(FluidResource resource) {
        return resource.fullyMatches(this.containedFluid.get(), this.containedFluid.getNBT());
    }

    @Override
    public int amount() {
        if(this.context.resource().is(this.fullItem)) {
            return this.containedAmount;
        }

        return 0;
    }

    @Override
    public FluidResource resource() {
        if(this.context.resource().is(this.fullItem)) {
            return this.containedFluid;
        }

        return FluidResource.empty();
    }

    @Override
    public int getCapacity(FluidResource resource) {
        return this.amount();
    }
}
