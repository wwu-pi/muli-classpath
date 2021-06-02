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
            Map<String, Object> coverMap = tc.getCoverMap();
            for(Map.Entry<String, Object> entry : coverMap.entrySet()) {
                boolean[] coverageArray = (boolean[]) entry.getValue();
                String method = entry.getKey();
                if(lengthMap.containsKey(method)){
                    int length = lengthMap.get(method);
                    if(length < coverageArray.length){
                        lengthMap.put(method, coverageArray.length);
                    }
                } else {
                    lengthMap.put(method, coverageArray.length);
                }
            }
        }
        return lengthMap;
    }
}
