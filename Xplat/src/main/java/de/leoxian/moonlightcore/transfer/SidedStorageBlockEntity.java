package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.transfer.item.ItemResource;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface SidedStorageBlockEntity {

    @Nullable
    default Storage<FluidResource> getFluidStorage(@Nullable Direction direction) {
        return null;
    }

    @Nullable
    default Storage<ItemResource> getItemStorage(@Nullable Direction direction) {
        return null;
    }

}
