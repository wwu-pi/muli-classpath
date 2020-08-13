package de.wwu.muli.tcg.testsetsorter;

import de.wwu.muli.solution.TestCase;

import java.util.Set;
import java.util.SortedSet;

public interface TestSetSorter {

    SortedSet<TestCase<?>> sortTestCases(Set<TestCase<?>> testCases);

}
