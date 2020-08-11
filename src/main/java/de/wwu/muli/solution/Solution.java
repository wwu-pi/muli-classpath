package de.wwu.muli.solution;

public class Solution<T> {
    public final T value;
    public final Object[] inputs;
    public TestCase testCase;

    public Solution(T value) {
        this(value, null);
    }

    public Solution(T value, Object[] inputs) {
        this.value = value;
        this.inputs = inputs;
    }

    public boolean isExceptionControlFlow() {
        return false;
    }

    public TestCase generateTestCase() {

        return null; // TODO
    }
}