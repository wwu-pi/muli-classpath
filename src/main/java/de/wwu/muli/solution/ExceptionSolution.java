package de.wwu.muli.solution;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExceptionSolution extends Solution<Object> {
    public ExceptionSolution(Object value) {
        super(value);
    }

    public ExceptionSolution(Object value, LinkedHashMap<String, Object> inputs, String className, String methodName, Map<String, Object> cover) {
        super(value, inputs, className, methodName, cover);
    }

    @Override
    public boolean isExceptionControlFlow() {
        return true;
    }
}
