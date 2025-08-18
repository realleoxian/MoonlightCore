package de.leowgc.moonlightcore.api.transfer;

public interface TransferResource<T> {

    T get();

    int amount();

    TransferResource<T> copy();

    default boolean isBlank() {
        return this.amount() == 0;
    }

}
