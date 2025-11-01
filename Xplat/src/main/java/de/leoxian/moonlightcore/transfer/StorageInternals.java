package de.leoxian.moonlightcore.transfer;

import org.jetbrains.annotations.ApiStatus;

import java.util.Iterator;
import java.util.NoSuchElementException;

@ApiStatus.Internal
public class StorageInternals {
    static <V, T extends TransferResource<V>> Iterator<StorageView<V, T>> singletonIterator(StorageView<V, T> resource) {
        return new Iterator<>() {
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return this.hasNext;
            }

            @Override
            public StorageView<V, T> next() {
                if(!this.hasNext) {
                    throw new NoSuchElementException();
                }
                this.hasNext = false;

                return resource;
            }
        };
    }

    public static void checkNonEmptyNonNegative(TransferResource<?> resource, int value) {
        checkNonEmpty(resource);
        checkNonNegative(value);
    }

    public static void checkNonEmpty(TransferResource<?> resource) {
        if(resource.isEmpty()) {
            throw new IllegalArgumentException("Expected resource to be non-empty: " + resource);
        }
    }

    public static void checkNonNegative(int value) {
        if(value < 0) {
            throw new IllegalArgumentException("Expected value to be non-negative: " + value);
        }
    }


}
