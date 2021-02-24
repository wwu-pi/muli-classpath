package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

public class SimpleBackwardsTestSetReducer implements TestSetReducer {

    @Override @SuppressWarnings("unchecked")
    public Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases) {
        Set<TestCase<?>> result;
        try { // Try to preserve the characteristics of the set structure, e.g., order:
            result = (Set<TestCase<?>>) testCases.getClass().newInstance();
            result.addAll(testCases);
        } catch (IllegalAccessException | InstantiationException e) {
            result = new HashSet<>(testCases);
        }

        // Get the total achievable cover given the set of test cases:
        BitSet overallCover = calculateOverallCover(testCases);

        for (TestCase<?> tc : testCases) {
            result.remove(tc);
            BitSet newOverallCover = calculateOverallCover(result);
            // If the new set of test cases has a smaller overall cover, readd the test case:
            if (newOverallCover.cardinality() < overallCover.cardinality()) {
                result.add(tc);
            }
        }

        return null;
    }

    protected static BitSet calculateOverallCover(Set<TestCase<?>> testCases) {
        BitSet result = new BitSet();
        // Add the cover for each test case.
        for (TestCase<?> tc : testCases) {
            result.or(tc.getCover());
        }

        return result;
    }
}
