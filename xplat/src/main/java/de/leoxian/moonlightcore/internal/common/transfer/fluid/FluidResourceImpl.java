package de.leoxian.moonlightcore.internal.common.transfer.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.common.transfer.fluid.FluidResource;
import de.leoxian.moonlightcore.internal.common.transfer.StorageInternals;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;

public class FluidResourceImpl implements FluidResource {
    public static final FluidResource EMPTY = of(Fluids.EMPTY, DataComponentPatch.EMPTY);
    public static final Codec<FluidResource> CODEC = RecordCodecBuilder.create(i -> i.group(
            BuiltInRegistries.FLUID.holderByNameCodec().fieldOf("fluid").forGetter(FluidResource::typeHolder),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(FluidResource::componentsPatch)
    ).apply(i, FluidResource::of));
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidResource> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.FLUID), FluidResource::typeHolder,
            DataComponentPatch.STREAM_CODEC, FluidResource::componentsPatch,
            FluidResource::of
    );

    public static FluidResource of(Fluid fluid, DataComponentPatch componentPatch) {
        Objects.requireNonNull(fluid, "Fluid may not be 'null'");
        Objects.requireNonNull(componentPatch, "Component patch may not be 'null'");

        if (fluid == Fluids.EMPTY || componentPatch == DataComponentPatch.EMPTY) {
            return ((FluidResourceCache) fluid).moonlightcore$getCachedFluidResource();
        }
        return new FluidResourceImpl(fluid, componentPatch);
    }

    private final Fluid fluid;
    private final DataComponentPatch componentPatch;
    private final DataComponentMap components;
    private final int hashCode;

    public FluidResourceImpl(Fluid fluid, DataComponentPatch componentPatch) {
        this.fluid = fluid;
        this.componentPatch = componentPatch;
        this.components = componentPatch == DataComponentPatch.EMPTY ? DataComponentMap.EMPTY : PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, componentPatch);
        this.hashCode = Objects.hash(fluid, componentPatch);
    }

    @Override
    public Fluid fluid() {
        return this.fluid;
    }

    @Override
    public FluidResource applyPatch(DataComponentPatch patch) {
        return of(this.fluid, StorageInternals.mergePatches(this.componentPatch, patch));
    }

    @Override
    public DataComponentPatch componentsPatch() {
        return this.componentPatch;
    }

    @Override
    public boolean isEmpty() {
        return this.fluid == Fluids.EMPTY;
    }

    @Override
    public Holder<Fluid> typeHolder() {
        return this.fluid.builtInRegistryHolder();
    }

    @Override
    public DataComponentMap getComponents() {
        return this.components;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj.getClass() != this.getClass()) return false;
        FluidResourceImpl other = (FluidResourceImpl) obj;
        return this.fluid == other.fluid &&
                this.componentPatch.equals(other.componentPatch) &&
                this.hashCode == other.hashCode;
    }

    @Override
    public String toString() {
        return "FluidResource[fluid, " + this.fluid + ", components=" + this.componentPatch + "]";
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
