package de.realleoxian.moonlightcore.api.transfer.fluid;

import com.mojang.serialization.Codec;
import de.realleoxian.moonlightcore.api.transfer.Resource;
import de.realleoxian.moonlightcore.impl.transfer.fluid.FluidResourceImpl;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public interface FluidResource extends Resource<Fluid> {
    Codec<FluidResource> CODEC = FluidResourceImpl.CODEC;

    static FluidResource blank() {
        return FluidResourceImpl.blank();
    }

    static FluidResource of(Fluid fluid, @Nullable CompoundTag tag) {
        return FluidResourceImpl.of(fluid, tag);
    }

    static FluidResource of(Fluid fluid) {
        return of(fluid, null);
    }

    static FluidResource fromNBT(CompoundTag nbt) {
        return FluidResourceImpl.fromNBT(nbt);
    }

    static FluidResource fromBuffer(FriendlyByteBuf byteBuf) {
        return FluidResourceImpl.fromBuffer(byteBuf);
    }

    @Override
    default boolean isBlank() {
        return get() == Fluids.EMPTY;
    }

}
