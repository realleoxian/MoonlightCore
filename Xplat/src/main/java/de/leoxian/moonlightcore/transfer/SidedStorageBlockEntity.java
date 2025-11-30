package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.core.Direction;

public interface SidedStorageBlockEntity {

    default @Nullable Storage<FluidResource> getFluidStorage(@Nullable Direction direction) {
        return null;
    }

    default @Nullable Storage<ItemResource> getItemStorage(@Nullable Direction direction) {
        return null;
    }

}
