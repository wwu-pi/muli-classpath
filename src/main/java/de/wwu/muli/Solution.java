package de.wwu.muli;

public class Solution<T> {
    // TODO besser: subtypen
    public final T value;
    public final Throwable exceptionValue;

    public final boolean isExceptionControlFlow;

    public Solution(T value) {
        this.value = value;
        this.exceptionValue = null;
        this.isExceptionControlFlow = false;
    }
    public Solution(Throwable value) {
        this.exceptionValue = value;
        this.value = null;
        this.isExceptionControlFlow = true;
    }
}
