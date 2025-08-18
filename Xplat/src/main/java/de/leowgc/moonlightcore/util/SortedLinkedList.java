package de.leowgc.moonlightcore.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;

public class SortedLinkedList<E> implements Collection<E>, Iterable<E> {
    private final Comparator<? super E> sorter;

    private Node head = null;
    private int size = 0;

    public SortedLinkedList(Comparator<? super E> sorter) {
        this.sorter = sorter;
        this.head = null;
        this.size = 0;
    }

    @Override
    public boolean add(E e) {
        if (e == null) return false;

        Node newNode = new Node(e);

        if (head == null || sorter.compare(head.data, e) >= 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && sorter.compare(current.next.data, e) < 0) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }

        size++;
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        boolean changed = false;
        for(E e : c) {
            changed |= this.add(e);
        }

        return changed;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null || head == null) return false;

        if (Objects.equals(head.data, o)) {
            head = head.next;
            size--;
            return true;
        }

        Node current = head;
        while (current.next != null) {
            if (Objects.equals(current.next.data, o)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        if (c.isEmpty() || head == null) return false;

        Set<?> lookup = (c instanceof Set<?> set) ? set : new HashSet<>(c);
        return bulkRemove(lookup::contains);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        if (head == null) return false;

        if (c.isEmpty()) {
            clear();
            return true;
        }

        Set<?> lookup = (c instanceof Set<?> set) ? set : new HashSet<>(c);
        return bulkRemove(data -> !lookup.contains(data));
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        return bulkRemove(filter);
    }

    @Override
    public boolean contains(Object o) {
        if(o == null) {
            return false;
        }

        Node current = this.head;
        while (current != null) {
            if(Objects.equals(current.data, o)) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for(Object o : c) {
            if(!this.contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (E e : this) {
            result[i++] = e;
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <T> T[] toArray(@NotNull T[] a) {
        if(a.length < this.size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size);
        }

        int i = 0;
        for(E e : this) {
            a[i++] = (T) e;
        }

        if(a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public void clear() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new IteratorImpl();
    }

    @Override
    public boolean isEmpty() {
        return this.head == null;
    }

    @Override
    public @NotNull Spliterator<E> spliterator() {
        return Spliterators.spliterator(iterator(), this.size, Spliterator.ORDERED);
    }

    private boolean bulkRemove(Predicate<? super E> predicate) {
        boolean changed = false;
        Node prev = null;
        Node current = head;

        while (current != null) {
            if (predicate.test(current.data)) {
                if (prev == null) {
                    head = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                changed = true;
            } else {
                prev = current;
            }
            current = current.next;
        }
        return changed;
    }

    private final class IteratorImpl implements Iterator<E> {
        private Node current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            E data = current.data;
            current = current.next;
            return data;
        }
    }

    private final class Node {
        final E data;
        Node next;

        Node(E data) {
            this.data = data;
        }
    }
}
