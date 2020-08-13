package de.wwu.muli.tcg.testmethodgenerator;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.utility.Indentator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StdTestMethodGenerator implements TestMethodGenerator { // TODO method name...
    protected final Class<?>[] specialCases;
    protected final String assertEqualsDelta;
    protected final Set<Class<?>> encounteredTypes;
    // Temporary store for objects and their argument name in the test method
    protected final Map<Object, String> argumentNamesForObjects = new HashMap<>();
    // An identifier for the currently generated test
    protected int numberOfTest = 0;
    // Name of class for which test cases are generated
    protected String testedClassName;
    // Name of method for which test cases are generated
    protected String testedMethodName;

    protected Indentator indentator;

    public StdTestMethodGenerator(Indentator indentator) {
        this(indentator, "10e-6");
    }

    public StdTestMethodGenerator(Indentator indentator, String assertEqualsDelta, Class<?>... specialCases) {
        this.assertEqualsDelta = assertEqualsDelta;
        this.specialCases = specialCases;
        encounteredTypes = new HashSet<>();
        this.indentator = indentator;
    }

    @Override
    public String generateTestCaseStringRepresentation(TestCase<?> tc) {
        init(tc);
        String result = execute(tc);
        tearDown(tc);
        return result;
    }

    protected void init(TestCase<?> tc) {
        if (testedClassName != null && testedMethodName != null) {
            if (!testedClassName.equals(tc.getClassName()) || !testedMethodName.equals(tc.getMethodName())) {
                throw new UnsupportedOperationException("Test cases can only be generated for one specific method " +
                        "at a time.");
            }
        } else {
            testedClassName = tc.getClassName();
            testedMethodName = tc.getMethodName();
        }
    }

    protected void tearDown(TestCase<?> tc) {
        argumentNamesForObjects.clear();
        numberOfTest++;
    }

    protected void addToImports(Class<?> classToImport) {
        encounteredTypes.add(classToImport);
    }

    public Set<Class<?>> getEncounteredTypes() {
        return encounteredTypes;
    }

    protected String execute(TestCase<?> tc) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateTestMethodAnnotations(tc));
        sb.append(generateTestMethodDeclaration(tc));
        sb.append(indentator.indentBlock(generateStringsForInputs(tc.getInputs())));
        sb.append(indentator.indentBlock(generateStringForOutput(tc.getOutput())));
        sb.append(indentator.indentBlock(generateAssertionString())); // TODO Method call && result
        sb.append(generateTestMethodEnd());
        return sb.toString();
    }

    protected String generateTestMethodAnnotations(TestCase<?> tc) {
        if (tc.getOutput() instanceof Exception) {
            return generateTestAnnotationForException((Exception) tc.getOutput());
        } else {
            return generateTestAnnotationForReturn(tc.getOutput());
        }
    }

    protected String generateTestAnnotationForException(Exception e) {
        return ""; // TODO
    }

    protected String generateTestAnnotationForReturn(Object returnVal) {
        return ""; // TODO
    }

    protected String generateTestMethodDeclaration(TestCase<?> tc) {
        return ""; // TODO
    }

    protected String generateTestMethodEnd() {
        return ""; // TODO
    }

    protected String generateStringsForInputs(Object[] inputs) {
        StringBuilder sb = new StringBuilder();
        for (Object input : inputs) {
            sb.append(generateElementString(input));
        }
        return sb.toString();
    }

    protected String generateElementString(Object o) {
        if (isNull(o)) {
            return generateNullString(o);
        } else if (isPrimitiveOrWrapping(o)) {
            return generatePrimitiveOrWrappingString(o);
        } else {
            return generateObjectString(o);
        }
    }

    protected boolean isNull(Object o) {
        return false; // TODO
    }

    protected String generateNullString(Object o) {
        return ""; // TODO
    }

    protected boolean isPrimitiveOrWrapping(Object o) {
        return false; // TODO
    }

    protected String generatePrimitiveOrWrappingString(Object o) {
        return ""; // TODO
    }

    protected String generateObjectString(Object o) {
        StringBuilder sb = new StringBuilder();
        if (isSpecialCase(o.getClass())) {
            return generateSpecialCaseString(o);
        }
        sb.append(generateConstructionString(o));
        sb.append(generateAttributesStrings(o));

        return sb.toString(); // TODO
    }

    protected boolean isSpecialCase(Class<?> objectClass) {
        for (Class<?> specialCase : specialCases) {
            if (objectClass.equals(specialCase)) {
                return true;
            }
        }
        return false;
    }

    protected String generateSpecialCaseString(Object o) {
        throw new UnsupportedOperationException("No special cases for the StdTestCaseGenerator yet.");
    }

    protected String generateConstructionString(Object o) {
        return ""; // TODO
    }

    protected String generateAttributesStrings(Object o) {
        return ""; // TODO
    }

    protected String generateStringForOutput(Object output) { // TODO
        return generateElementString(output);
    }

    protected String generateAssertionString() { // TODO
        return ""; // TODO
    }

    protected String generateTestedMethodCallString() { // TODO
        return ""; // TODO
    }

    @Override
    public String getTestedClassName() {
        return testedClassName;
    }

    @Override
    public String getTestedMethodName() {
        return testedMethodName;
    }

}
