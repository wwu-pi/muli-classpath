package de.wwu.muli.solution;

import java.util.BitSet;
import java.util.LinkedHashMap;

public class TestCase<T> {
    private static int testCounter = 0;
    private final int testNumber;
    private final String methodName;
    private final String className;
    private final String fullClassName;
    private final LinkedHashMap<String, Object> inputs;
    private final T output;
    private final BitSet cover;

    public TestCase(LinkedHashMap<String, Object> inputs, T output, String fullClassName, String methodName, BitSet cover) {
        testNumber = testCounter++;
        this.inputs = inputs;
        this.output = output;
        this.fullClassName = fullClassName;
        this.className = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        this.methodName = methodName;
        this.cover = cover;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public Object[] getInputs() {
        return inputs.values().toArray(new Object[0]);
    }

    public Object getInput(String argName) {
        return inputs.get(argName);
    }

    public LinkedHashMap<String, Object> getNamesAndInputs() {
        return inputs;
    }

    public BitSet getCover() {
        return cover;
    }

    public T getOutput() {
        return output;
    }

    public int getTestNumber() {
        return testNumber;
    }
}
