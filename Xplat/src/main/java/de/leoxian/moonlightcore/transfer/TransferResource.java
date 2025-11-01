package de.leoxian.moonlightcore.transfer;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface TransferResource<T> {

    T get();

    boolean isEmpty();

    @Nullable
    CompoundTag getNBT();

    default boolean hasNBT() {
        return getNBT() != null;
    }

    @Nullable
    default CompoundTag copyNBT() {
        CompoundTag nbt = this.getNBT();
        return nbt == null ? null : nbt.copy();
    }

    default boolean nbtMatches(CompoundTag nbt) {
        return Objects.equals(this.getNBT(), nbt);
    }

    default boolean is(T t) {
        return get() == t;
    }

    default boolean fullyMatches(T t, @Nullable CompoundTag nbt) {
        return nbt == null ? is(t) : (is(t) && nbtMatches(nbt));
    }
}
