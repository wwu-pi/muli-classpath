package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.*;

public class SimpleBackwardsTestSetReducer implements TestSetReducer {

    @Override @SuppressWarnings("unchecked")
    public Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases) {
        Set<TestCase<?>> result;
        // Try to preserve the characteristics of the set structure, e.g., order:
        try { // Try to preserve the characteristics of the set structure, e.g., order:
            result = (Set<TestCase<?>>) testCases.getClass().newInstance();
            result.addAll(testCases);
        } catch (IllegalAccessException | InstantiationException e) {
            result = new HashSet<>(testCases);
        }

        Map<String, Integer> coverageLength = getLengthMap(testCases);

        // Get the total achievable cover given the set of test cases:
        BitSet overallCover = calculateOverallCover(testCases, coverageLength);

        for (TestCase<?> tc : testCases) {
            result.remove(tc);
            BitSet newOverallCover = calculateOverallCover(result, coverageLength);
            // If the new set of test cases has a smaller overall cover, readd the test case:
            if (newOverallCover.cardinality() < overallCover.cardinality()) {
                result.add(tc);
            }
        }

        return result;
    }

    protected static BitSet calculateOverallCover(Set<TestCase<?>> testCases, Map<String, Integer> coverageLength) {
        BitSet result = new BitSet();
        // Add the cover for each test case.
        for (TestCase<?> tc : testCases) {
            result.or(tc.getCover(coverageLength));
        }

        return result;
    }

    protected static Map<String, Integer> getLengthMap(Set<TestCase<?>> testCases){
        Map<String, Integer> lengthMap = new LinkedHashMap<>();
        for (TestCase<?> tc : testCases) {
            Map<String, int[]> coverMap = tc.getCoverMap();
            for(Map.Entry<String, int[]> entry : coverMap.entrySet()) {
                int[] coverageArray = entry.getValue();
                String method = entry.getKey();
                int maxNumber = Arrays.stream(coverageArray).max().orElseGet(() -> 0);
                if(lengthMap.containsKey(method)){
                    int length = lengthMap.get(method);
                    if(length < maxNumber){
                        lengthMap.put(method, maxNumber);
                    }
                } else {
                    lengthMap.put(method, maxNumber);
                }
            }
        }
        return lengthMap;
    }
}
