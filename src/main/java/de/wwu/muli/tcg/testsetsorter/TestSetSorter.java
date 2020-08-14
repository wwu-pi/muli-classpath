package de.wwu.muli.tcg.testsetsorter;

import de.wwu.muli.solution.TestCase;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public abstract class TestSetSorter {

    protected final Comparator<TestCase<?>> comparator;

    public TestSetSorter(Comparator<TestCase<?>> comparator) {
        this.comparator = comparator;
    }

    public SortedSet<TestCase<?>> sortTestCases(Set<TestCase<?>> testCases) {
        SortedSet<TestCase<?>> result = new TreeSet<>(comparator);
        result.addAll(testCases);
        return result;
    }

}
