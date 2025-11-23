package de.leoxian.moonlightcore.transfer;

public interface StorageView<T> extends StorageIO<T> {

    int getCapacity(T resource);

    int getAmount();

    boolean isResourceBlank();

    T getResource();

    default ResourceStack<T> toStack() {
        return new ResourceStack<>(this.getResource(), this.getAmount());
    }

}
