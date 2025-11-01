package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

public class EmptyStorage<V, T extends TransferResource<V>> implements Storage<V, T> {
    private static final EmptyStorage<?, ?> INSTANCE = new EmptyStorage<>();

    @SuppressWarnings("unchecked")
    public static <V, T extends TransferResource<V>> Storage<V, T> empty() {
        return (EmptyStorage<V, T>) INSTANCE;
    }

    private EmptyStorage() {}

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull StorageView<V, T> get(int index) {
        return null;
    }

    @Override
    public @NotNull Iterator<StorageView<V, T>> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        return 0;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        return 0;
    }
}
