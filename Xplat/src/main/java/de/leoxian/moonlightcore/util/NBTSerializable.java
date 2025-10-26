package de.leoxian.moonlightcore.util;

import net.minecraft.nbt.Tag;

public interface NBTSerializable<T extends Tag> {

    T writeToNBT();

    void readFromNBT(T tag);

}
