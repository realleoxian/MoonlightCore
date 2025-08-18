package de.leowgc.moonlightcore.api.transfer;

public interface SlottedStorage<T> extends Storage<T> {

    int insertIntoSlot(Transaction transaction, int slot, TransferResource<T> resource);

    TransferResource<T> extractFromSlot(Transaction transaction, int slot, T resourceType, int maxAmount);

    TransferResource<T> getResourceInSlot(int slot);

    void setSlotCapacity(int slot, int capacity);

    int getSlotCapacity(int slot);

    int getSlotCount();

}
