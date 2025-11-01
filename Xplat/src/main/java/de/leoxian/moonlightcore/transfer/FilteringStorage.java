package de.leoxian.moonlightcore.transfer;

import com.google.common.collect.Iterators;
import de.leoxian.moonlightcore.transfer.transaction.Transaction;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Supplier;

public abstract class FilteringStorage<T extends TransferResource<?>> implements Storage<T> {

    public static <T extends TransferResource<?>> Storage<T> insertOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, true, false);
    }

    public static <T extends TransferResource<?>> Storage<T> extractOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, false, true);
    }

    public static <T extends TransferResource<?>> Storage<T> readOnlyOf(Storage<T> backingStorage) {
        return of(backingStorage, false, false);
    }

    public static <T extends TransferResource<?>> Storage<T> of(Storage<T> backingStorage, boolean allowInsert, boolean allowExtract) {
        if(allowInsert && allowExtract) {
            return backingStorage;
        }

        return new FilteringStorage<T>(() -> backingStorage) {
            @Override
            protected boolean canInsert(T resource) {
                return allowInsert;
            }

            @Override
            protected boolean canExtract(T resource) {
                return allowExtract;
            }

            @Override
            public boolean supportsInsertion() {
                return allowInsert && super.supportsInsertion();
            }

            @Override
            public boolean supportsExtraction() {
                return allowExtract && super.supportsExtraction();
            }
        };
    }

    protected final Supplier<Storage<T>> backingStorage;

    protected FilteringStorage(Supplier<Storage<T>> backingStorage) {
        this.backingStorage = backingStorage;
    }

    protected abstract boolean canInsert(T resource);

    protected abstract boolean canExtract(T resource);

    @Override
    public int insert(Transaction tx, T resource, int amount) {
        if(this.canInsert(resource)) {
            return this.backingStorage.get().insert(tx, resource, amount);
        }

        return 0;
    }

    @Override
    public int extract(Transaction tx, T resource, int amount) {
        if(this.canExtract(resource)) {
            return this.backingStorage.get().extract(tx, resource, amount);
        }

        return 0;
    }

    @Override
    public @NotNull StorageView<T> get(int index) {
        return this.backingStorage.get().get(index);
    }

    @Override
    public boolean supportsInsertion() {
        return this.backingStorage.get().supportsInsertion();
    }

    @Override
    public boolean supportsExtraction() {
        return this.backingStorage.get().supportsExtraction();
    }

    @Override
    public int size() {
        return this.backingStorage.get().size();
    }

    @Override
    public @NotNull Iterator<StorageView<T>> iterator() {
        return Iterators.transform(this.backingStorage.get().iterator(), FilteringStorageView::new);
    }

    private class FilteringStorageView implements StorageView<T> {
        private final StorageView<T> backingView;

        private FilteringStorageView(StorageView<T> backingView) {
            this.backingView = backingView;
        }

        @Override
        public int extract(Transaction tx, T resource, int amount) {
            if(!canExtract(resource)) {
                return this.backingView.extract(tx, resource, amount);
            }

            return 0;
        }

        @Override
        public int insert(Transaction tx, T resource, int amount) {
            if(!canInsert(resource)) {
                return this.backingView.insert(tx, resource, amount);
            }

            return 0;
        }

        @Override
        public boolean isResourceValid(T resource) {
            return this.backingView.isResourceValid(resource);
        }

        @Override
        public int getCapacity(T resource) {
            return this.backingView.getCapacity(resource);
        }

        @Override
        public T resource() {
            return this.backingView.resource();
        }

        @Override
        public int amount() {
            return this.backingView.amount();
        }
    }
}
