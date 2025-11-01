package de.leoxian.moonlightcore.transfer.fluid;

import de.leoxian.moonlightcore.transfer.EmptyStorageView;
import de.leoxian.moonlightcore.transfer.InsertionOnlyStorage;
import de.leoxian.moonlightcore.transfer.StorageInternals;
import de.leoxian.moonlightcore.transfer.StorageView;
import de.leoxian.moonlightcore.transfer.context.ItemStorageContext;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class EmptyItemFluidStorage implements InsertionOnlyStorage<Fluid, FluidResource> {

    private final List<StorageView<Fluid, FluidResource>> emptyView;
    private final ItemStorageContext context;
    private final Function<ItemResource, ItemResource> emptyToFullMapping;
    private final Item emptyItem;
    private final Fluid insertableFluid;
    private final int insertableAmount;

    public EmptyItemFluidStorage(ItemStorageContext context, Item fullItem, Fluid insertableFluid, int insertableAmount) {
        this(context, emptyResource -> ItemResource.of(fullItem, emptyResource.getNBT()), insertableFluid, insertableAmount);
    }

    public EmptyItemFluidStorage(ItemStorageContext context, Function<ItemResource, ItemResource> emptyToFullMapping, Fluid insertableFluid, int insertableAmount) {
        StorageInternals.checkNonNegative(insertableAmount);

        this.context = context;
        this.emptyToFullMapping = emptyToFullMapping;
        this.emptyItem = context.resource().get();
        this.insertableFluid = insertableFluid;
        this.insertableAmount = insertableAmount;
        this.emptyView = List.of(new EmptyStorageView<>(FluidResource.empty(), insertableAmount));
    }

    @Override
    public int insert(Transaction tx, FluidResource resource, int amount) {
        StorageInternals.checkNonEmptyNonNegative(resource, amount);

        if(!this.context.resource().is(this.emptyItem)) {
            return 0;
        }

        if(resource.is(this.insertableFluid) && amount >= this.insertableAmount) {
            ItemResource newResource = this.emptyToFullMapping.apply(this.context.resource());

            if(context.exchange(tx, newResource, 1) == 1) {
                return insertableAmount;
            }
        }

        return 0;
    }


    @Override
    public int size() {
        return 1;
    }

    @Override
    public @NotNull StorageView<Fluid, FluidResource> get(int index) {
        return this.emptyView.get(0);
    }

    @Override
    public @NotNull Iterator<StorageView<Fluid, FluidResource>> iterator() {
        return this.emptyView.iterator();
    }
}
