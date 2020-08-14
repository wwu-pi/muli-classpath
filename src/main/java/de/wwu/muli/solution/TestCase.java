package de.wwu.muli.solution;

import java.util.BitSet;

public class TestCase<T> {
    private String methodName;
    private String className;
    private String fullClassName;
    private Object[] inputs;
    private T output;
    private BitSet coveredInstructions;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public Object[] getInputs() {
        return inputs;
    }

    public void setInputs(Object[] inputs) {
        this.inputs = inputs;
    }

    public BitSet getCoveredInstructions() {
        return coveredInstructions;
    }

    public void setCoveredInstructions(BitSet coveredInstructions) {
        this.coveredInstructions = coveredInstructions;
    }

    public void setOutput(T output) {
        this.output = output;
    }

    public T getOutput() {
        return output;
    }
}
