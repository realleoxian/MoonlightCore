package de.leoxian.moonlightcore.api.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class SortedLinkedList<E> implements Collection<E>, Iterable<E> {
    private final Comparator<E> comparator;

    private Node<E> root;
    private int size;

    public SortedLinkedList(Comparator<E> comparator) {
        this.comparator = comparator;

        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean add(E e) {
        if(this.root == null) {
            this.root = new Node<>(e);
        } else {
            if(this.comparator.compare(e, this.root.data) <= 0) {
                this.root = new Node<>(e, this.root);
            } else {
                Node<E> previousNode = this.root;
                Node<E> currentNode = this.root.next;

                while(currentNode != null && this.comparator.compare(e, currentNode.data) >= 0) {
                    previousNode = currentNode;
                    currentNode = currentNode.next;
                }

                previousNode.next = new Node<>(e, currentNode);
            }
        }

        this.size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if(this.root == null) {
            return false;
        }

        if(Objects.equals(o, this.root.data)) {
            this.root = this.root.next;
            this.size--;
            return true;
        }

        Node<E> previousNode = this.root;
        Node<E> currentNode = this.root.next;

        while(currentNode != null) {
            if(Objects.equals(o, currentNode.data)) {
                previousNode.next = currentNode.next;
                this.size--;

                return true;
            }

            previousNode = currentNode;
            currentNode = currentNode.next;
        }

        return false;
    }

    @Override
    public boolean contains(Object o) {
        Node<E> currentNode = this.root;

        while(currentNode != null) {
            if(Objects.equals(o, currentNode.data)) {
                return true;
            }

            currentNode = currentNode.next;
        }

        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        boolean changed = false;

        Node<E> previousNode = null;
        Node<E> currentNode = this.root;

        while (currentNode != null) {
            Node<E> nextNode = currentNode.next;

            if(!c.contains(currentNode.data)) {
                if(previousNode == null) {
                    this.root = nextNode;
                } else {
                    previousNode.next = nextNode;
                }

                changed = true;
            }

            previousNode = currentNode;
            currentNode = nextNode;
        }

        return changed;
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
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean changed = false;
        for(Object o : c) {
            changed |= this.remove(o);
        }

        return changed;
    }

    @Override
    public boolean removeIf(@NotNull Predicate<? super E> filter) {
        return Collection.super.removeIf(filter);
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
        Object[] result = new Object[this.size];
        int i = 0;

        for(Node<E> x = this.root; x != null; x = x.next) {
            result[i++] = x.data;
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if(a == null) {
            throw new NullPointerException("Input array cannot be null");
        }

        if(a.length < this.size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        }

        int i = 0;
        for(Node<E> x = this.root; x != null; x = x.next) {
            a[i++] = (T) x.data;
        }

        if(a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        for(Node<E> x = this.root; x != null; x = x.next) {
            action.accept(x.data);
        }
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return new IteratorImpl<>(this.root);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data, Node<E> next) {
            this.data = data;
        }

        Node(E data) {
            this(data, null);
        }
    }

    private static class IteratorImpl<E> implements Iterator<E> {
        private Node<E> currentNode;

        IteratorImpl(Node<E> root) {
            this.currentNode = root;
        }

        @Override
        public boolean hasNext() {
            return this.currentNode != null;
        }

        @Override
        public E next() {
            if(!hasNext()) throw new NoSuchElementException();
            E data = this.currentNode.data;
            this.currentNode = this.currentNode.next;

            return data;
        }
    }
}
