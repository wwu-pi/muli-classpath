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
                int[] coverageArray = (int[]) entry.getValue();
                String method = entry.getKey();
                int current_max = coverageArray[coverageArray.length-1];
                if(lengthMap.containsKey(method)){
                    int max = lengthMap.get(method);
                    if(max < current_max){
                        lengthMap.put(method, current_max);
                    }
                } else {
                    lengthMap.put(method, current_max);
                }
            }
        }
        return lengthMap;
    }
}
