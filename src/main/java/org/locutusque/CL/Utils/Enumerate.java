package org.locutusque.CL.Utils;

import org.locutusque.CL.Dynamic;
import org.locutusque.CL.Exceptions.NoSuchObjectError;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Enumerate<T> extends Dynamic implements Iterable<T> {
    private final Iterable<T> iterable;

    public Enumerate(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return new EnumerateIterator<>(iterable.iterator(), iterable);
    }

    private static class EnumerateIterator<T> implements Iterator<T> {
        private final Iterator<T> iterator;
        private int index;
        private Iterable<T> iterable;

        public EnumerateIterator(Iterator<T> iterator, Iterable<T> iterable) {
            this.iterator = iterator;
            this.index = 0;
            this.iterable = iterable;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            if (!hasNext()) {
                try {
                    throw new NoSuchObjectError(iterable.toString());
                } catch (NoSuchObjectError e) {
                    e.printStackTrace();
                }
            }
            return iterator.next();
        }
    }
}



