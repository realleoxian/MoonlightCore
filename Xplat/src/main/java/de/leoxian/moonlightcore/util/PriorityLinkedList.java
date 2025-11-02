package de.leoxian.moonlightcore.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class PriorityLinkedList<T> implements Iterable<T>, Collection<T> {
    private static final int DEFAULT_PRIORITY = 1000;
    private final SortedLinkedList<PriorityLinkedList.Entry<T>> entries;

    public PriorityLinkedList() {
        this.entries = new SortedLinkedList<>((a, b) -> b.priority - a.priority);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return addAll(c, DEFAULT_PRIORITY);
    }

    public boolean addAll(@NotNull Collection<? extends T> c, int priority) {
        boolean changed = false;
        for(T t : c) {
            changed |= add(t, priority);
        }

        return changed;
    }

    public boolean add(T t, int priority) {
        return this.entries.add(new Entry<>(t, priority));
    }

    @Override
    public boolean add(T t) {
        return this.add(t, DEFAULT_PRIORITY);
    }

    @Override
    public boolean contains(Object o) {
        return this.entries.contains(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return this.entries.containsAll(c);
    }

    @Override
    public boolean remove(Object o) {
        return this.entries.remove(o);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return this.entries.removeAll(c);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return this.entries.retainAll(c);
    }

    @Override
    public void clear() {
        this.entries.clear();
    }

    @Override
    public int size() {
        return this.entries.size();
    }

    @Override
    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new PriorityLinkedListIterator<>(this.entries.iterator());
    }

    @Override
    public @NotNull Object[] toArray() {
        Object[] result = new Object[size()];
        int i = 0;

        for(Entry<T> entry : this.entries) {
            result[i++] = entry.value;
        }

        return result;
    }

    @Override
    public @NotNull <T1> T1[] toArray(@NotNull T1[] a) {
        if(a.length < size()) {
            a = (T1[]) Array.newInstance(a.getClass().getComponentType(), size());
        }

        int i = 0;
        Object[] result = a;
        for(Entry<T> entry : this.entries) {
            result[i++] = entry.value;
        }

        if(a.length > size()) {
            a[size()] = null;
        }

        return a;
    }

    private record PriorityLinkedListIterator<T>(Iterator<Entry<T>> iterator) implements Iterator<T> {
        @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public T next() {
                return this.iterator.next().value;
            }
        }

    private record Entry<T>(T value, int priority) {

        @Override
            public boolean equals(Object obj) {
                if (this == obj) return true;
                if (obj instanceof Entry<?> other) {
                    return Objects.equals(value, other.value);
                } else if (obj != null) {
                    return obj.equals(value);
                }

            return false;
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.value);
            }

            @Override
            public String toString() {
                return value.toString() + " - " + this.priority;
            }

        }
}
