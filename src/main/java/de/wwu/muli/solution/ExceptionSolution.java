package de.wwu.muli.solution;

import java.util.BitSet;
import java.util.LinkedHashMap;

public class ExceptionSolution extends Solution<Object> {
    public ExceptionSolution(Object value) {
        super(value);
    }

    public ExceptionSolution(Object value, LinkedHashMap<String, Object> inputs, String className, String methodName, boolean[] cover) {
        super(value, inputs, className, methodName, cover);
    }

    @Override
    public boolean isExceptionControlFlow() {
        return true;
    }
}
