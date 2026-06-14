package de.realleoxian.moonlightcore.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ImmutableIterable<T> implements Iterable<T> {
    private final Iterable<T> it;

    public ImmutableIterable(Iterable<T> it) {
        this.it = it;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<T>() {
            final Iterator<T> iterator = ImmutableIterable.this.it.iterator();

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
