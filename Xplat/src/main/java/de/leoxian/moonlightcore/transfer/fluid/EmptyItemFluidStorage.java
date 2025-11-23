package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.BlankResourceView;
import de.leoxian.moonlightcore.transfer.InsertionOnlyStorage;
import de.leoxian.moonlightcore.transfer.StoragePreconditions;
import de.leoxian.moonlightcore.transfer.StorageView;
import de.leoxian.moonlightcore.transfer.context.ContainerItemContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class EmptyItemFluidStorage implements InsertionOnlyStorage<FluidResource> {
    private final ContainerItemContext context;
    private final Item emptyItem;
    private final Function<ItemResource, ItemResource> emptyToFullMapping;
    private final Fluid insertableFluid;
    private final int insertableAmount;
    private final List<StorageView<FluidResource>> blankView;

    public EmptyItemFluidStorage(ContainerItemContext context, Function<ItemResource, ItemResource> emptyToFullMapping, Fluid insertableFluid, int insertableAmount) {
        this.context = context;
        this.emptyItem = context.getResource().getResource();
        this.emptyToFullMapping = emptyToFullMapping;
        this.insertableFluid = insertableFluid;
        this.insertableAmount = insertableAmount;
        this.blankView = List.of(new BlankResourceView<>(FluidResource.blank(), insertableAmount));
    }

    public EmptyItemFluidStorage(ContainerItemContext context, Item fullItem, Fluid insertableFluid, int insertableAmount) {
        this(context, emptyResource -> ItemResource.of(fullItem, emptyResource.getNBT()), insertableFluid, insertableAmount);
    }

    @Override
    public int insert(TransactionContext context, FluidResource insertedResource, int maxAmount) {
        StoragePreconditions.notBlankNotNegative(insertedResource, maxAmount);
        if(!this.context.getResource().isOf(this.emptyItem)) return 0;

        if(insertedResource.isOf(insertableFluid) && maxAmount >= insertableAmount) {
            ItemResource newResource = emptyToFullMapping.apply(this.context.getResource());

            if(this.context.exchange(context, newResource, 1) == 1) {
                return insertableAmount;
            }
        }

        return 0;
    }

    @Override
    public StorageView<FluidResource> get(int index) {
        if(index != 0) {
            throw new IndexOutOfBoundsException("Slot " + index + " does not exist in empty bucket storage");
        }

        return this.blankView.get(0);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public @Nonnull Iterator<StorageView<FluidResource>> iterator() {
        return this.blankView.iterator();
    }

    @Override
    public String toString() {
        return "EmptyItemFluidStorage[context=%s, insertableFluid=%s, insertableAmount=%d]".formatted(context, insertableFluid, insertableAmount);
    }
}
