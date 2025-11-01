package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public interface SidedStorageBlockEntity {

    @Nullable
    default Storage<Fluid, FluidResource> getFluidStorage(@Nullable Direction direction) {
        return null;
    }

    @Nullable
    default Storage<Item, ItemResource> getItemStorage(@Nullable Direction direction) {
        return null;
    }

}
