package de.wwu.muli;

public class Solution {
    public final Object value;
    public final boolean isExceptionControlFlow;

    public Solution(Object value, boolean isExceptionControlFlow) {
        this.value = value;
        this.isExceptionControlFlow = isExceptionControlFlow;
    }
}
