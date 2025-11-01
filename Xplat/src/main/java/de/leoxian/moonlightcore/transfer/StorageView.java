package de.leoxian.moonlightcore.transfer;

public interface StorageView<V, T extends TransferResource<V>> extends StorageIO<T> {

    boolean isResourceValid(T resource);

    int getCapacity(T resource);

    T resource();

    int amount();

    default ResourceStack<V, T> toStack() {
        return new ResourceStack<>(this.resource(), this.amount());
    }

    default boolean isEmpty() {
        return StorageUtils.isEmpty(this.resource(), this.amount());
    }

}
