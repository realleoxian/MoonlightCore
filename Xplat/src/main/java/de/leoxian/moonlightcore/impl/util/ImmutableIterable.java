package de.leoxian.moonlightcore.impl.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ImmutableIterable<T> implements Iterable<T> {
    private final Iterable<T> backingIterable;

    public ImmutableIterable(Iterable<T> backingIterable) {
        this.backingIterable = backingIterable;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Iterator<T> iterator = ImmutableIterable.this.backingIterable.iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }
}
