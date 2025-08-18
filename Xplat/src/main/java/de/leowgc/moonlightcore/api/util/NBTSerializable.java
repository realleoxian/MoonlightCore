package de.leowgc.moonlightcore.api.util;

import net.minecraft.nbt.Tag;

public interface NBTSerializable<T extends Tag> {

    T toNBT();

    void fromNBT(T nbt);

}
