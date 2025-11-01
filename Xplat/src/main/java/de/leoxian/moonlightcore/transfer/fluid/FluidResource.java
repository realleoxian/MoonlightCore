package de.leoxian.moonlightcore.transfer.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.transfer.TransferResource;
import de.leoxian.moonlightcore.transfer.TransferResourceExtension;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public final class FluidResource implements TransferResource<Fluid> {
    private static final FluidResource EMPTY = of(Fluids.EMPTY);

    public static final Codec<FluidResource> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidResource::get),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter((resource) -> Optional.ofNullable(resource.getNBT()))
    ).apply(instance, (fluid, tag) -> of(fluid, tag.orElse(null))));

    public static FluidResource empty() {
        return EMPTY;
    }

    public static FluidResource of(Fluid fluid) {
        return of(fluid, null);
    }

    @SuppressWarnings("unchecked")
    public static FluidResource of(Fluid fluid, @Nullable CompoundTag tag) {
        Objects.requireNonNull(fluid, "Fluid cannot be null");

        if(tag == null || fluid == Fluids.EMPTY) {
            return ((TransferResourceExtension<FluidResource>) fluid).mlcore_getCachedResource();
        }

        return new FluidResource(fluid, tag);
    }

    private final Fluid fluid;
    @Nullable
    private final CompoundTag tag;

    public FluidResource(Fluid fluid, @Nullable CompoundTag tag) {
        this.fluid = fluid;
        this.tag = tag;
    }

    @Override
    public Fluid get() {
        return this.fluid;
    }

    @Override
    public boolean isEmpty() {
        return this.fluid == Fluids.EMPTY;
    }

    @Override
    public @Nullable CompoundTag getNBT() {
        return this.tag;
    }
}
