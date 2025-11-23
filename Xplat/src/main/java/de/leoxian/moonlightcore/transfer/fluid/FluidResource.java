package de.leoxian.moonlightcore.transfer.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.leoxian.moonlightcore.transfer.TransferResource;
import de.leoxian.moonlightcore.util.ByteBufCodecs;
import de.leoxian.moonlightcore.util.StreamCodec;
import de.leoxian.moonlightcore.util.nullness.Nullable;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Objects;
import java.util.Optional;

public class FluidResource implements TransferResource<Fluid> {
    private static final FluidResource BLANK = new FluidResource(Fluids.EMPTY, null);

    public static final Codec<FluidResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(resource -> resource.fluid),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(resource -> Optional.ofNullable(resource.tag))
    ).apply(instance, (fluid, tag) -> new FluidResource(fluid, tag.orElse(null))));

    public static final StreamCodec<ByteBuf, FluidResource> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    public static FluidResource blank() {
        return BLANK;
    }

    public static FluidResource of(Fluid fluid) {
        return of(fluid, null);
    }

    public static FluidResource of(Fluid fluid, @Nullable CompoundTag tag) {
        Objects.requireNonNull(fluid);

        if(!fluid.isSource(fluid.defaultFluidState()) && fluid != Fluids.EMPTY) {
            if(fluid instanceof FlowingFluid flowingFluid){
                fluid = flowingFluid.getSource();
            } else{
                String errorMessage = String.format(
                        "Cannot convert flowing fluid %s (%s) into a source fluid",
                        BuiltInRegistries.FLUID.getKey(fluid),
                        fluid
                );

                throw new IllegalArgumentException(errorMessage);
            }
        }

        if(tag == null || fluid == Fluids.EMPTY) {
            return ((FluidResourceCache) fluid).mlcore_getCachedFluidResource();
        } else {
            return new FluidResource(fluid, tag);
        }
    }

    public static FluidResource fromNBT(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT may not be null");

        try {
            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(nbt.getString("fluid")));
            @Nullable CompoundTag tag = nbt.contains("tag") ? nbt.getCompound("tag") : null;

            return of(fluid, tag);
        } catch (Exception e) {
            return FluidResource.blank();
        }
    }

    private final Fluid fluid;
    private final @Nullable CompoundTag tag;
    private final int hashCode;

    public FluidResource(Fluid fluid, @Nullable CompoundTag tag) {
        this.fluid = fluid;
        this.tag = tag == null ? null : tag.copy();
        this.hashCode = Objects.hash(fluid, tag);
    }

    @Override
    public Fluid getResource() {
        return fluid;
    }

    @Override
    public boolean isBlank() {
        return this.fluid == Fluids.EMPTY;
    }

    @Override
    public @Nullable CompoundTag getNBT() {
        return this.tag;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag result = new CompoundTag();
        result.putString("fluid", BuiltInRegistries.FLUID.getKey(this.fluid).toString());

        if(tag != null) {
            result.put("tag", this.tag.copy());
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        FluidResource other = (FluidResource) obj;
        return hashCode == other.hashCode && fluid == other.fluid && nbtMatches(other.tag);
    }

    @Override
    public String toString() {
        return "FluidResource[fluid=" + this.fluid + ", tag=" + this.tag + "]";
    }
}
