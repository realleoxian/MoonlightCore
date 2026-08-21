package de.leoxian.moonlightcore.common.transfer.fluid;

import com.mojang.serialization.Codec;
import de.leoxian.moonlightcore.common.transfer.resource.RegisteredResourceWithData;
import de.leoxian.moonlightcore.internal.common.transfer.fluid.FluidResourceImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface FluidResource extends RegisteredResourceWithData<Fluid> {
    FluidResource EMPTY = FluidResourceImpl.EMPTY;
    Codec<FluidResource> CODEC = FluidResourceImpl.CODEC;
    StreamCodec<RegistryFriendlyByteBuf, FluidResource> STREAM_CODEC = FluidResourceImpl.STREAM_CODEC;

    static FluidResource of(Fluid fluid, DataComponentPatch componentPatch) {
        return FluidResourceImpl.of(fluid, componentPatch);
    }

    static FluidResource of(Holder<Fluid> holder, DataComponentPatch componentPatch) {
        return of(holder.value(), componentPatch);
    }

    static FluidResource of(Fluid fluid) {
        return of(fluid, DataComponentPatch.EMPTY);
    }

    static FluidResource of(Holder<Fluid> holder) {
        return of(holder.value(), DataComponentPatch.EMPTY);
    }

    @Override
    FluidResource applyPatch(DataComponentPatch componentPatch);

    Fluid fluid();
}
