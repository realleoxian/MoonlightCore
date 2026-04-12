package de.realleoxian.moonlightcore.api.transfer.fluid;

import de.realleoxian.moonlightcore.api.apilookup.block.BlockApiLookup;
import de.realleoxian.moonlightcore.api.transfer.SidedStorageBlockEntity;
import de.realleoxian.moonlightcore.api.transfer.storage.Storage;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public final class FluidStorage {
    public static final BlockApiLookup<Storage<FluidResource>, @Nullable Direction> SIDED = BlockApiLookup.find(new ResourceLocation("moonlightcore", "sided_fluid_storage"), Storage.asClass(), Direction.class);

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
