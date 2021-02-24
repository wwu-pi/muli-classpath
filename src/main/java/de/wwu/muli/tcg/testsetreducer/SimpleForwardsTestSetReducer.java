package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class SimpleForwardsTestSetReducer implements TestSetReducer {

    @Override
    public Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases) {
        Set<TestCase<?>> result = new HashSet<>();
        // Currently no cover at all:
        BitSet currentCover = new BitSet();
        // If the coverage of the current cover with the cover of the current test case is the same as before do not add it:
        for (TestCase<?> tc : testCases) {
            BitSet newCover = (BitSet) currentCover.clone();
            newCover.or(tc.getCover());
            if (currentCover.cardinality() < newCover.cardinality()) {
                result.add(tc);
            }
        }

        return result;
    }
}
