package de.wwu.muli.tcg.testsetreducer;

import de.wwu.muli.solution.TestCase;

import java.util.*;

public class SimpleForwardsTestSetReducer implements TestSetReducer {

    @Override
    public Set<TestCase<?>> reduceTestSet(Set<TestCase<?>> testCases) {
        Set<TestCase<?>> result = new HashSet<>();
        // Currently no cover at all:
        BitSet currentCover = new BitSet();
        Map<String, Integer> coverageLength = getLengthMap(testCases);
        // If the coverage of the current cover with the cover of the current test case is the same as before do not add it:
        for (TestCase<?> tc : testCases) {
            BitSet newCover = (BitSet) currentCover.clone();
            newCover.or(tc.getCover(coverageLength));
            if (currentCover.cardinality() < newCover.cardinality()) {
                result.add(tc);
                currentCover = newCover;
            }
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
