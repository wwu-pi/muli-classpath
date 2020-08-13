package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.Set;

public interface TestSetReducer {

    Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases);

}
