package de.wwu.muli.solution;

public class ExceptionSolution extends Solution<Object> {
    public ExceptionSolution(Object value) {
        super(value);
    }

    public ExceptionSolution(Object value, Object[] inputs) {
        super(value, inputs);
    }

    @Override
    public boolean isExceptionControlFlow() {
        return true;
    }
}
