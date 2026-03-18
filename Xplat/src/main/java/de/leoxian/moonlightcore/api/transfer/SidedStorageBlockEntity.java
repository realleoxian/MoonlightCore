package de.leoxian.moonlightcore.api.transfer;


import de.leoxian.moonlightcore.api.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.api.transfer.item.ItemResource;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

public interface SidedStorageBlockEntity {

    @ApiStatus.OverrideOnly
    default @Nullable Storage<ItemResource> getItemStorage(@Nullable Direction direction) {
        return null;
    }

    @ApiStatus.OverrideOnly
    default @Nullable Storage<FluidResource> getFluidStorage(@Nullable Direction direction) {
        return null;
    }

}
