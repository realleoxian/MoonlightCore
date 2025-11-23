package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.ExtractionOnlyStorage;
import de.leoxian.moonlightcore.transfer.SingleSlotStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class FullItemFluidStorage implements ExtractionOnlyStorage<FluidResource>, SingleSlotStorage<FluidResource> {
    private final ContainerItemContext context;
    private final Item fullItem;
    private final Function<ItemResource, ItemResource> fullToEmptyMapping;
    private final FluidResource containedFluid;
    private final int containedAmount;

    public FullItemFluidStorage(ContainerItemContext context, Item emptyItem, FluidResource containedFluid, int containedAmount) {
        this(context, fullResource -> ItemResource.of(emptyItem, fullResource.getNBT()), containedFluid, containedAmount);
    }

    public FullItemFluidStorage(ContainerItemContext context, Function<ItemResource, ItemResource> fullToEmptyMapping, FluidResource containedFluid, int containedAmount) {
        StoragePreconditions.notBlankNotNegative(containedFluid, containedAmount);

        this.context = context;
        this.fullItem = context.getResource().getResource();
        this.fullToEmptyMapping = fullToEmptyMapping;
        this.containedFluid = containedFluid;
        this.containedAmount = containedAmount;
    }

    @Override
    public int extract(TransactionContext context, FluidResource extractedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(extractedResource, maxAmount);
        if(!this.context.getResource().isOf(fullItem)) return 0;

        if(extractedResource.equals(containedFluid) && maxAmount >= containedAmount) {
            ItemResource newResource = fullToEmptyMapping.apply(this.context.getResource());

            if(this.context.exchange(context, newResource, 1) == 1) {
                return containedAmount;
            }
        }

        return 0;
    }

    @Override
    public FluidResource getResource() {
        if(this.context.getResource().isOf(fullItem)) {
            return containedFluid;
        }

        return FluidResource.blank();
    }

    @Override
    public int getAmount() {
        if(this.context.getResource().isOf(fullItem)) {
            return containedAmount;
        }

        return 0;
    }

    @Override
    public boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    public int getCapacity(FluidResource resource) {
        return getAmount();
    }

    @Override
    public String toString() {
        return "FullItemFluidStorage[context=%s, fluid=%s, amount=%d]".formatted(this.context, this.containedFluid, this.containedAmount);
    }
}
