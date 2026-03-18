package de.leoxian.moonlightcore.api.transfer;

import de.leoxian.moonlightcore.impl.util.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public interface Resource<T> {

    T get();

    boolean isBlank();

    @Nullable
    CompoundTag getTag();

    default @Nullable CompoundTag copyTag() {
        CompoundTag tag = getTag();
        return tag == null ? null : tag.copy();
    }

    default CompoundTag copyTagOrCreate() {
        CompoundTag tag = getTag();
        return tag == null ? new CompoundTag() : tag.copy();
    }

    default boolean tagMatches(@Nullable CompoundTag other) {
        return Objects.equals(getTag(), other);
    }

    default boolean hasTag() {
        return getTag() != null;
    }

    default boolean is(T t) {
        return t == get();
    }

    void writeToBuffer(FriendlyByteBuf byteBuf);

}
