package de.leoxian.moonlightcore.common.transfer.capability;

import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.common.transfer.item.ItemResource;
import de.leoxian.moonlightcore.common.transfer.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

public interface SidedStorageBlockEntity {
    /// Return an item storage if available on the required side, or `null` otherwise
    /// @param direction The direction the storage to query
    @ApiStatus.OverrideOnly
    default @Nullable Storage<ItemResource> getItemStorage(Direction direction) {
        return null;
    }

    /// Return a fluid storage if available on the required side, or `null` otherwise
    /// @param direction The direction the storage to query
    @ApiStatus.OverrideOnly
    default @Nullable Storage<FluidResource> getFluidStorage(Direction direction) {
        return null;
    }
}
