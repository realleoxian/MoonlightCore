package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.util.nullness.Nullable;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

public interface TransferResource<T> {

    T getResource();

    boolean isBlank();

    @Nullable CompoundTag getNBT();

    CompoundTag toNBT();

    default boolean isOf(T t) {
        return getResource() == t;
    }

    default boolean hasNBT() {
        return getNBT() != null;
    }

    default boolean nbtMatches(@Nullable CompoundTag other){
        return Objects.equals(this.getNBT(), other);
    }

    default @Nullable CompoundTag copyNBT() {
        CompoundTag nbt = getNBT();
        return nbt == null ? null : nbt.copy();
    }

    default CompoundTag getOrCopyNBT() {
        CompoundTag nbt = getNBT();
        return nbt == null ? toNBT() : nbt.copy();
    }

}
