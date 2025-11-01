package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public interface Storage<T extends TransferResource<?>> extends StorageIO<T>, Iterable<StorageView<T>> {

    @SuppressWarnings("unchecked")
    static <T extends TransferResource<?>> Class<Storage<T>> asClass() {
        return (Class<Storage<T>>) (Object) Storage.class;
    }

    int size();

    @NotNull
    StorageView<T> get(int index);

    @Override
    @NotNull Iterator<StorageView<T>> iterator();

    default Iterator<StorageView<T>> nonEmptyIterator() {
        return Iterators.filter(this.iterator(), (view) -> view.amount() > 0 && !view.resource().isEmpty());
    }

    default Iterable<StorageView<T>> nonEmptyViews() {
        return this::nonEmptyIterator;
    }

    default int insert(Transaction tx, int index, T resource, int amount) {
        Objects.checkIndex(index, this.size());
        return get(index).insert(tx, resource, amount);
    }

    default int extract(Transaction tx, int index, T resource, int amount) {
        Objects.checkIndex(index, this.size());
        return get(index).extract(tx, resource, amount);
    }

    default T getResource(int index) {
        Objects.checkIndex(index, this.size());
        return this.get(index).resource();
    }

    default ResourceStack<T> toStack(int index) {
        Objects.checkIndex(index, this.size());
        return this.get(index).toStack();
    }

    default int getAmount(int index) {
        Objects.checkIndex(index, this.size());
        return this.get(index).amount();
    }

    default int getLimit(int index, T resource) {
        Objects.checkIndex(index, this.size());
        return this.get(index).getCapacity(resource);
    }

    default boolean isResourceValid(int index, T resource) {
        Objects.checkIndex(index, this.size());
        return this.get(index).isResourceValid(resource);
    }

    default boolean supportsInsertion() {
        return true;
    }

    default boolean supportsExtraction() {
        return true;
    }

}
