package de.wwu.muli.tcg.testmethodgenerator;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.utility.Indentator;

import java.lang.reflect.Field;
import java.util.*;

import static de.wwu.muli.tcg.utility.Utility.*;

// TODO get fully qualified class name which is tested
public class StdTestMethodGenerator implements TestMethodGenerator {
    protected final Class<?>[] specialCases;
    protected final String assertEqualsDelta;
    protected final Set<Class<?>> encounteredTypes;
    // Temporary store for objects and their argument name in the test method
    // Should be cleared after the String for the TestCase-object was generated.
    protected final Map<Object, String> argumentNamesForObjects = new HashMap<>();
    // Temporary stores for object types (represented as strings) how many of them are already within a method.
    // Should be cleared after the String for the TestCase-object was generated.
    protected final Map<String, Integer> argumentNameToNumberOfOccurrences = new HashMap<>();
    // Temporary stores all input objects
    protected Object[] inputObjects = null;
    // Temporary stores all output objects;
    protected Object outputObject = null;
    // An identifier for the currently generated test
    protected int numberOfTest = 0;
    // Name of class for which test cases are generated
    protected String testedClassName;
    // Name of method for which test cases are generated
    protected String testedMethodName;
    protected Indentator indentator;

    public StdTestMethodGenerator(Indentator indentator) {
        this(indentator, "10e-6", Collection.class, Map.class);
    }

    public StdTestMethodGenerator(Indentator indentator, String assertEqualsDelta, Class<?>... specialCases) {
        this.assertEqualsDelta = assertEqualsDelta;
        this.specialCases = specialCases;
        encounteredTypes = new HashSet<>();
        this.indentator = indentator;
    }

    @Override
    public String generateTestCaseStringRepresentation(TestCase<?> tc) {
        before(tc);
        String result = execute(tc);
        after(tc);
        return result;
    }

    protected void before(TestCase<?> tc) {
        if (testedClassName != null && testedMethodName != null) {
            if (!testedClassName.equals(tc.getClassName()) || !testedMethodName.equals(tc.getMethodName())) {
                throw new UnsupportedOperationException("Test cases can only be generated for one specific method " +
                        "at a time.");
            }
        } else {
            testedClassName = tc.getClassName();
            testedMethodName = tc.getMethodName();
        }

        inputObjects = tc.getInputs();
        outputObject = tc.getOutput();
        argumentNamesForObjects.put(null, "null");
    }

    protected void after(TestCase<?> tc) {
        argumentNamesForObjects.clear();
        argumentNameToNumberOfOccurrences.clear();
        inputObjects = null;
        outputObject = null;
        numberOfTest++;
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
        sb.append(indentator.indentBlock(generateAssertionString()));
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
        return "@Test(expected=" + e.getClass().getName() + ".class)\r\n";
    }

    protected String generateTestAnnotationForReturn(Object returnVal) {
        return "@Test\r\n";
    }

    protected String generateTestMethodDeclaration(TestCase<?> tc) {
        return "public void test_"+ tc.getMethodName() + "_" + numberOfTest + "() {\r\n";
    }

    protected String generateTestMethodEnd() {
        return "}\r\n";
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
            return ""; // No element string has to be generated for null
        }
        if (isAlreadyCreated(o)) {
            return "";
        }
        encounteredTypes.add(o.getClass());
        generateNumberedArgumentName(o);
        if (isPrimitiveClass(o.getClass())) {
            return generatePrimitiveString(o);
        } else if (isWrappingClass(o.getClass())) {
            return generateWrappingString(o);
        } else if (isStringClass(o.getClass())) {
            return generateStringString(o);
        } else {
            return generateObjectString(o);
        }
    }

    protected boolean isAlreadyCreated(Object o) {
        return argumentNamesForObjects.containsKey(o);
    }

    protected String generateNullString(Object o) {
        return "null;\r\n";
    }

    protected String generatePrimitiveString(Object o) {
        // TODO Parameter type is bad...Object leads to auto-wrapping to Integer; is this problematic?
        return ""; // TODO
    }

    protected String generateWrappingString(Object o) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getSimpleName());
            sb.append(" ");
            sb.append(argumentNamesForObjects.get(o));
            sb.append(" = ");
            Field valueField = o.getClass().getDeclaredField("value");
            boolean accessible = valueField.isAccessible();
            valueField.setAccessible(true);
            sb.append(addCastIfNeeded(o.getClass()));
            sb.append(valueField.get(o)).append(";\r\n");
            valueField.setAccessible(accessible);
            return sb.toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean wrappingClassNeedsCast(Class<?> oc) {
        return oc.equals(Float.class) || oc.equals(Short.class) || oc.equals(Byte.class);
    }

    protected String addCastIfNeeded(Class<?> oc) {
        if (wrappingClassNeedsCast(oc)) {
            // Float, Byte, or Short
            return "(" + toFirstLower(oc.getSimpleName()) + ") ";
        } else {
            return "";
        }
    }

    protected String generateNumberedArgumentName(Object o) {
        String numberedArgumentNameForType = argumentNamesForObjects.get(o);
        if (numberedArgumentNameForType != null) {
            return numberedArgumentNameForType;
        }
        Class<?> type = o.getClass();
        String argumentNameForType = getArgumentNameForType(type);
        Integer currentNumberOfArgumentType = argumentNameToNumberOfOccurrences.get(argumentNameForType);
        if (currentNumberOfArgumentType == null) {
            currentNumberOfArgumentType = 0;
        }
        numberedArgumentNameForType = argumentNameForType + currentNumberOfArgumentType;
        argumentNameToNumberOfOccurrences.put(argumentNameForType, currentNumberOfArgumentType + 1);
        argumentNamesForObjects.put(o, numberedArgumentNameForType);
        return numberedArgumentNameForType;
    }

    protected String getArgumentNameForType(Class<?> type) {
        return toFirstLower(type.getSimpleName());
    }

    protected String generateObjectString(Object o) {
        StringBuilder sb = new StringBuilder();
        if (isSpecialCase(o.getClass())) {
            return generateSpecialCaseString(o);
        }
        sb.append(generateConstructionString(o));
        sb.append(generateAttributesStrings(o));
        return sb.toString();
    }

    protected boolean isSpecialCase(Class<?> objectClass) {
        for (Class<?> specialCase : specialCases) {
            if (specialCase.isAssignableFrom(objectClass)) {
                return true;
            }
        }
        return false;
    }

    protected String generateSpecialCaseString(Object o) {
        if (o instanceof Collection) {
            return treatIterableSpecialCase((Collection<?>) o);
        } else if (o instanceof Map) {
            return treatMapSpecialCase((Map<?, ?>) o);
        }
        throw new UnsupportedOperationException("Aside from Collection and Map there currently are no special cases " +
                "for the StdTestCaseGenerator yet.");
    }

    protected String treatIterableSpecialCase(Collection<?> c) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateConstructionString(c));
        String collectionName = argumentNamesForObjects.get(c);
        for (Object o : c) {
            sb.append(generateElementString(o));
            sb.append(collectionName).append(".add(").append(argumentNamesForObjects.get(o)).append(");\r\n");
        }
        return sb.toString();
    }

    protected String treatMapSpecialCase(Map<?, ?> m) {
        StringBuilder sb = new StringBuilder();
        sb.append(generateConstructionString(m));
        String mapName = argumentNamesForObjects.get(m);
        for (Map.Entry<?, ?> e : m.entrySet()) {
            sb.append(generateElementString(e.getKey()));
            sb.append(generateElementString(e.getValue()));
            String keyName = argumentNamesForObjects.get(e.getKey());
            String valueName = argumentNamesForObjects.get(e.getValue());
            sb.append(mapName).append(".put(").append(keyName).append(", ").append(valueName).append(");\r\n");
        }
        return sb.toString();
    }

    protected String generateStringString(Object o) {
        return "String " + argumentNamesForObjects.get(o) + " = \"" + o.toString() + "\";\r\n";
    }

    protected String generateConstructionString(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getSimpleName()).append(" ");
        sb.append(argumentNamesForObjects.get(o)).append(" = ");
        sb.append("new ").append(o.getClass().getSimpleName()).append("();\r\n");
        return sb.toString();
    }

    protected String generateAttributesStrings(Object o) {
        StringBuilder sb = new StringBuilder();
        Field[] fields = o.getClass().getDeclaredFields();
        String objectArgumentName = argumentNamesForObjects.get(o);
        for (Field f : fields) {
            sb.append(generateSetStatementForObject(objectArgumentName, o, f));
        }
        return sb.toString();
    }

    protected String generateSetStatementForObject(String objectArgumentName, Object o, Field f) {
        try {
            StringBuilder sb = new StringBuilder();
            boolean accessible = f.isAccessible();
            f.setAccessible(true);
            Object fieldValue = f.get(o);
            String fieldName = f.getName();
            f.setAccessible(accessible);
            sb.append(generateElementString(fieldValue));
            String fieldValueArgumentName = argumentNamesForObjects.get(fieldValue);
            sb.append(objectArgumentName)
                    .append(".set")
                    .append(toFirstUpper(fieldName))
                    .append("(")
                    .append(fieldValueArgumentName)
                    .append(");\r\n");
            return sb.toString();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    protected String generateStringForOutput(Object output) {
        return generateElementString(output);
    }

    protected String generateAssertionString() {
        String[] inputObjectNames = new String[inputObjects.length];
        for (int i = 0; i < inputObjectNames.length; i++) {
            inputObjectNames[i] = argumentNamesForObjects.get(inputObjects[i]);
        }
        String outputObjectName = argumentNamesForObjects.get(outputObject);
        StringBuilder sb = new StringBuilder();
        if (outputObject != null) {
            sb.append("assertEquals(").append(outputObjectName).append(", ");
        }
        sb.append(generateTestedMethodCallString("TODO", inputObjectNames)); // TODO
        if (outputObject != null) {
            if (isFloatingPointClass(outputObject.getClass())) {
                sb.append(", ").append(assertEqualsDelta);
            }
            sb.append(")");
        }
        sb.append(";\r\n");
        return sb.toString();
    }

    protected String generateTestedMethodCallString(String qualifiedClassName, String[] inputObjectNames) {
        StringBuilder sb = new StringBuilder();
        sb.append(qualifiedClassName).append(".").append(testedMethodName).append("(");
        for (String s : inputObjectNames) {
            sb.append(s).append(",");
        }
        sb.delete(sb.length()-1, sb.length());
        sb.append(")");
        return sb.toString();
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
