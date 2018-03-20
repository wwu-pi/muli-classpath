package de.wwu.muli.search;

import de.wwu.muli.solution.Solution;

import java.util.Iterator;
import java.util.function.Supplier;

public class SolutionIterator<T> implements Iterator<Solution<T>> {
    private final Supplier<Solution<T>> searchRegion;
    private Object detrail; // Might be relocated inside VM.

    public SolutionIterator(Supplier<Solution<T>> searchRegion) {
        this.searchRegion = searchRegion;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Solution<T> next() {
        return null;
    }
}
