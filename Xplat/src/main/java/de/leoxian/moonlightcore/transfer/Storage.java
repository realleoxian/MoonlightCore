package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;

import java.util.Iterator;
import java.util.Objects;

public interface Storage<T> extends StorageIO<T>, Iterable<StorageView<T>> {

    @SuppressWarnings("unchecked")
    static <T> Storage<T> empty() {
        return (Storage<T>) EmptyStorage.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    static <T> Class<Storage<T>> asClass() {
        return (Class<Storage<T>>) (Object) Storage.class;
    }

    StorageView<T> get(int index);

    int size();

    @Nonnull Iterator<StorageView<T>> iterator();

    default Iterable<StorageView<T>> nonEmptyViews() {
        return this::nonEmptyIterator;
    }

    default Iterator<StorageView<T>> nonEmptyIterator() {
        return Iterators.filter(this.iterator(), view -> view.getAmount() > 0 && !view.isResourceBlank());
    }

    default int insert(TransactionContext context, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, this.size());
        return get(index).insert(context, resource, maxAmount);
    }

    default int simulateInsert(TransactionContext context, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, this.size());
        return get(index).simulateInsert(context, resource, maxAmount);
    }

    default boolean supportsInsertion() {
        return true;
    }

    default int extract(TransactionContext context, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, this.size());
        return get(index).extract(context, resource, maxAmount);
    }

    default int simulateExtract(TransactionContext context, int index, T resource, int maxAmount) {
        Objects.checkIndex(index, this.size());
        return get(index).simulateExtract(context, resource, maxAmount);
    }

    default boolean supportsExtraction() {
        return true;
    }

    default T getResource(int index) {
        Objects.checkIndex(index, this.size());
        return get(index).getResource();
    }

    default int getAmount(int index) {
        Objects.checkIndex(index, this.size());
        return get(index).getAmount();
    }

    default int getCapacity(int index, T resource) {
        Objects.checkIndex(index, this.size());
        return get(index).getCapacity(resource);
    }

    default boolean isBlank(int index) {
        Objects.checkIndex(index, this.size());
        return get(index).isResourceBlank();
    }

    default ResourceStack<T> toStack(int index) {
        Objects.checkIndex(index, this.size());
        return get(index).toStack();
    }

}
