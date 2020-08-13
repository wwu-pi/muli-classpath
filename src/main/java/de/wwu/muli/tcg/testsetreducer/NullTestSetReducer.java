package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.Set;

public class NullTestSetReducer implements TestSetReducer {

    @Override
    public Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases) {
        return testCases;
    }
}
