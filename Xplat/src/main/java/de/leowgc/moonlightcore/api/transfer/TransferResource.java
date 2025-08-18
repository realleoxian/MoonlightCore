package de.leowgc.moonlightcore.api.transfer;

import de.leowgc.moonlightcore.api.util.NBTSerializable;
import de.leowgc.moonlightcore.api.util.NetworkSerializable;
import net.minecraft.nbt.CompoundTag;

public interface TransferResource<T> extends NBTSerializable<CompoundTag>, NetworkSerializable {

    T get();

    int amount();

    TransferResource<T> copy();

    default boolean isBlank() {
        return this.amount() == 0;
    }

}
