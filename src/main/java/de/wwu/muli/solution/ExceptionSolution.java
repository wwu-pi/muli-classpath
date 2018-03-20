package de.wwu.muli.solution;

public class ExceptionSolution extends Solution<Object> {
    public ExceptionSolution(Object value) {
        super(value);
    }

    @Override
    public boolean isExceptionControlFlow() {
        return true;
    }
}
