package de.leoxian.moonlightcore.util;

import de.leoxian.moonlightcore.util.nullness.Nullable;

import java.util.function.Supplier;

public final class LazySupplier<T> implements Supplier<T> {

    public static  <T> LazySupplier<T> of(Supplier<T> supplier) {
        return new LazySupplier<>(supplier);
    }

    private final Supplier<T> delegate;
    private volatile @Nullable T cachedValue;

    private LazySupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    public synchronized void invalidate() {
        this.cachedValue = null;
    }

    @Override
    public T get() {
        T ret = cachedValue;
        if(ret == null) {
            synchronized (this) {
                ret = cachedValue;

                if(ret == null) {
                    cachedValue = ret = delegate.get();

                    if(ret == null) {
                        throw new IllegalStateException("Lazy value cannot be null, but delegate returned null: " + delegate);
                    }
                }
            }
        }

        return cachedValue;
    }
}
