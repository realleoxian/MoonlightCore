package de.realleoxian.moonlightcore.impl.transfer.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.realleoxian.moonlightcore.api.transfer.fluid.FluidResource;
import de.realleoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class FluidResourceImpl implements FluidResource {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonlightcore-trasnfer-api/fluid");
    private static final FluidResource BLANK = new FluidResourceImpl(Fluids.EMPTY, null);

    public static final Codec<FluidResource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(resource -> ((FluidResourceImpl) resource).fluid),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(resource -> Optional.ofNullable(((FluidResourceImpl) resource).tag))
    ).apply(instance, (fluid, tag) -> of(fluid, tag.orElse(null))));

    private static final String TAG_FLUID = "fluid";
    private static final String TAG_TAG = "tag";

    public static FluidResource blank() {
        return BLANK;
    }

    public static FluidResource of(Fluid fluid, @Nullable CompoundTag tag) {
        Objects.requireNonNull(fluid, "Fluid may not be 'null'");

        if(fluid == Fluids.EMPTY || tag == null) {
            return ((FluidResourceCache) fluid).moonlightcore$getCachedFluidResource();
        }

        return new FluidResourceImpl(fluid, tag);
    }

    public static FluidResource fromNBT(CompoundTag nbt) {
        Objects.requireNonNull(nbt, "NBT may not be 'null'");

        try {
            Fluid fluid = BuiltInRegistries.FLUID.get(new ResourceLocation(nbt.getString(TAG_FLUID)));
            CompoundTag tag = nbt.contains(TAG_TAG) ? nbt.getCompound(TAG_TAG) : null;

            return of(fluid, tag);
        } catch (Exception e) {
            LOGGER.error("Failed to read FluidResource from NBT", e);
            return blank();
        }
    }

    public static FluidResource fromBuffer(FriendlyByteBuf byteBuf) {
        Objects.requireNonNull(byteBuf, "Buffer may not be 'null'");

        Fluid fluid = BuiltInRegistries.FLUID.byId(byteBuf.readVarInt());
        CompoundTag tag = byteBuf.readNbt();
        return of(fluid, tag);
    }

    private final Fluid fluid;
    private final @Nullable CompoundTag tag;
    private final int hashCode;

    public FluidResourceImpl(Fluid fluid, @Nullable CompoundTag tag) {
        this.fluid = fluid;
        this.tag = tag == null ? null : tag.copy();
        this.hashCode = Objects.hash(fluid, tag);
    }

    @Override
    public Fluid get() {
        return fluid;
    }

    @Override
    public @Nullable CompoundTag getTag() {
        return tag;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf byteBuf) {
        if(isBlank()) {
            byteBuf.writeBoolean(false);
            return;
        }

        byteBuf.writeBoolean(true);
        byteBuf.writeVarInt(BuiltInRegistries.FLUID.getId(fluid));
        byteBuf.writeNbt(tag);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj.getClass() != getClass()) return false;

        FluidResourceImpl other = (FluidResourceImpl) obj;
        return other.fluid == fluid && other.tag == tag;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "FluidResource[fluid=%s, tag=%s]".formatted(BuiltInRegistries.FLUID.getKey(fluid), tag);
    }
}
