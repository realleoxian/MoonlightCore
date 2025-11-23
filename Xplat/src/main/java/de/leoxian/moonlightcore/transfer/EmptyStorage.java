package de.leoxian.moonlightcore.transfer;

import de.leoxian.moonlightcore.transfer.transaction.TransactionContext;
import de.leoxian.moonlightcore.util.nullness.Nonnull;

import java.util.Collections;
import java.util.Iterator;

class EmptyStorage<T> implements Storage<T> {
    static final EmptyStorage<?> INSTANCE = new EmptyStorage<>();

    @Override
    public int insert(TransactionContext context, T insertedResource, int maxAmount) {
        return 0;
    }

    @Override
    public int extract(TransactionContext context, T extractedResource, int maxAmount) {
        return 0;
    }

    @Override
    public @Nonnull StorageView<T> get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public @Nonnull Iterator<StorageView<T>> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }
}
