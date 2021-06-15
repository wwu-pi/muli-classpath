package de.wwu.muli.solution;

import java.util.*;

public class TestCase<T> {
    private static int testCounter = 0;
    private final int testNumber;
    private final String methodName;
    private final String className;
    private final String fullClassName;
    private final LinkedHashMap<String, Object> inputs;
    private final T output;
    private final Map<String, int[]> cover;

    public TestCase(LinkedHashMap<String, Object> inputs, T output, String fullClassName, String methodName, Map<String, int[]> cover) {
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

    public BitSet getCover(Map<String, Integer> lengthMap) {
        BitSet result = new BitSet();
        for(Map.Entry<String, Integer> entry : lengthMap.entrySet()) {
            String method = entry.getKey();
            int[] coverageArray = new int[]{};
            if(cover.containsKey(method)){
                coverageArray = cover.get(method);
            }
            int length = entry.getValue();
            for (int i : coverageArray) {
                result.set(i);
            }
        }
        return result;
    }

    public Map<String, int[]> getCoverMap(){
        return cover;
    }

    public T getOutput() {
        return output;
    }

    public int getTestNumber() {
        return testNumber;
    }

    public String toString() {
        return "TestCase{"; //+ Arrays.toString(cover) + "}";
    }
}
