package de.leowgc.moonlightcore.api.transfer;

import de.leowgc.moonlightcore.api.util.NBTSerializable;
import de.leowgc.moonlightcore.transfer.VanillaContainerWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public interface Storage<T> extends NBTSerializable<CompoundTag> {

    static <T> int move(Storage<T> from, Storage<T> to, Transaction transaction, T resourceType, int maxAmount) {
        TransferResource<T> extracted = from.extract(transaction, resourceType, maxAmount);
        if(extracted.isBlank()) {
            return 0;
        }

        int inserted = to.insert(transaction, extracted);
        int leftover = extracted.amount() - inserted;

        if (leftover > 0) {
            throw new IllegalStateException("Destination couldn't accept full transfer!");
        }
        return inserted;
    }

    static Storage<ItemStack> vanillaWrapper(Container container) {
        return new VanillaContainerWrapper(container);
    }

    int insert(Transaction transaction, TransferResource<T> resource);

    TransferResource<T> extract(Transaction transaction, T resourceType, int maxAmount);

    default void writeToNBT(CompoundTag nbt) {
        nbt.put("moonlightcore.storage", this.toNBT());
    }

    default void readFromNBT(CompoundTag nbt) {
        if(nbt.contains("moonlightcore.storage", Tag.TAG_COMPOUND)) {
            this.fromNBT(nbt.getCompound("moonlightcore.storage"));
        }
    }

    default boolean supportsInsertion() {
        return true;
    }

    default boolean supportsExtraction() {
        return true;
    }

}

