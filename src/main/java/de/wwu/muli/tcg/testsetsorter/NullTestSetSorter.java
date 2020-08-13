package de.wwu.muli.tcg.testsetsorter;

import de.wwu.muli.solution.TestCase;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class NullTestSetSorter implements TestSetSorter {

    @Override
    public SortedSet<TestCase<?>> sortTestCases(Set<TestCase<?>> testCases) {
        return new TreeSet<>(testCases);
    }
}
