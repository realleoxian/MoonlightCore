package de.leoxian.moonlightcore.api.transfer.fluid;

import de.leoxian.moonlightcore.api.apilookup.BlockApiLookup;
import de.leoxian.moonlightcore.api.transfer.SidedStorageBlockEntity;
import de.leoxian.moonlightcore.api.transfer.storage.Storage;
import de.leoxian.moonlightcore.impl.internal.InternalMod;
import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Direction;

public final class FluidStorage {
    public static final BlockApiLookup<Storage<FluidResource>, @Nullable Direction> SIDED = BlockApiLookup.find(InternalMod.location("sided_fluid_storage"), Storage.asClass(), Direction.class);

    static {
        SIDED.registerFallback((level, blockPos, blockState, blockEntity, context) -> {
            if(blockEntity instanceof SidedStorageBlockEntity) {
                return ((SidedStorageBlockEntity) blockEntity).getFluidStorage(context);
            }

            return null;
        });
    }

    private FluidStorage() {}
}
