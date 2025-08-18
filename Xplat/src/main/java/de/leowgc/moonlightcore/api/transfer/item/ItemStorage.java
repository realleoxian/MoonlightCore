package de.leowgc.moonlightcore.api.transfer.item;

import de.leowgc.moonlightcore.api.transfer.SlottedStorage;
import de.leowgc.moonlightcore.api.transfer.Storage;
import de.leowgc.moonlightcore.transfer.item.EmptyItemStorage;
import de.leowgc.moonlightcore.transfer.item.SimpleItemStorage;
import de.leowgc.moonlightcore.transfer.item.SlottedItemStorage;
import net.minecraft.world.item.ItemStack;

public interface ItemStorage extends Storage<ItemStack> {


    static ItemStorage simple(int capacity) {
        return new SimpleItemStorage(capacity);
    }

    static ItemStorage emptyItemStorage() {
        return new EmptyItemStorage();
    }

    static SlottedStorage<ItemStack> slotted(int slotCount, int defaultSlotCapacity) {
        return new SlottedItemStorage(slotCount, defaultSlotCapacity);
    }

    static SlottedStorage<ItemStack> slotted(int slotCount) {
        return new SlottedItemStorage(slotCount, 64);
    }

}
