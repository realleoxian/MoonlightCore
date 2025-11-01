package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

public class EmptyStorage<T extends TransferResource<?>> implements Storage<T> {
    private static final EmptyStorage<?> INSTANCE = new EmptyStorage<>();

    @SuppressWarnings("unchecked")
    public static  <T extends TransferResource<?>> Storage<T> empty() {
        return (EmptyStorage<T>) INSTANCE;
    }

    private EmptyStorage() {}

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @NotNull StorageView<T> get(int index) {
        return null;
    }

    @Override
    public @NotNull Iterator<StorageView<T>> iterator() {
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
