package de.wwu.muli.solution;

import java.util.BitSet;
import java.util.LinkedHashMap;

public class Solution<T> {
    public final T value;
    public final TestCase<T> testCase;

    public Solution(T value) {
        this.value = value;
        this.testCase = null;
    }

    public Solution(T value, LinkedHashMap<String, Object> inputs, String className, String methodName, BitSet cover) {
        this.value = value;
        this.testCase = new TestCase<>(inputs, value, className, methodName, cover);
    }

    public boolean isExceptionControlFlow() {
        return false;
    }
}