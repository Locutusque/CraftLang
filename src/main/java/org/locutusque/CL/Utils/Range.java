package org.locutusque.CL.Utils;

import org.locutusque.CL.Dynamic;

import java.util.Iterator;

/**
 * Represents a range of integers. Implementation similar to Python's for CraftLang
 */
public class Range extends Dynamic implements Iterable<Integer> {
    private int start;
    private int end;
    private int step;

    /**
     * Constructs a range of integers.
     *
     * @param start The starting value (inclusive) of the range.
     * @param end   The ending value (exclusive) of the range.
     * @param step  The step size for iterating over the range.
     */
    public Range(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    /**
     * Constructs a range of integers with a default step size of 1.
     *
     * @param start The starting value (inclusive) of the range.
     * @param end   The ending value (exclusive) of the range.
     */
    public Range(int start, int end) {
        this(start, end, 1);
    }

    /**
     * Returns the starting value of the range.
     *
     * @return The starting value of the range.
     */
    public int getStart() {
        return start;
    }

    /**
     * Returns the ending value of the range.
     *
     * @return The ending value of the range.
     */
    public int getEnd() {
        return end;
    }

    /**
     * Returns the step size of the range.
     *
     * @return The step size of the range.
     */
    public int getStep() {
        return step;
    }

    /**
     * Returns an iterator over the range of integers.
     *
     * @return An iterator over the range of integers.
     */
    @Override
    public Iterator<Integer> iterator() {
        return new RangeIterator();
    }

    /**
     * An iterator for iterating over the range of integers.
     */
    private class RangeIterator implements Iterator<Integer> {
        private int current;

        /**
         * Constructs a range iterator.
         */
        public RangeIterator() {
            current = start;
        }

        /**
         * Checks if there is a next value in the range.
         *
         * @return true if there is a next value, false otherwise.
         */
        @Override
        public boolean hasNext() {
            if (step > 0) {
                return current < end;
            } else if (step < 0) {
                return current > end;
            } else {
                return false;
            }
        }

        /**
         * Returns the next value in the range.
         *
         * @return The next value in the range.
         */
        @Override
        public Integer next() {
            int value = current;
            current += step;
            return value;
        }
    }
}
