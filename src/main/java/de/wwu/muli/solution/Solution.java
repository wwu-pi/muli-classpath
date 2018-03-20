package de.wwu.muli.solution;

public class Solution<T> {
    public final T value;

    public Solution(T value) {
        this.value = value;
    }

    public boolean isExceptionControlFlow() {
        return false;
    }
}
