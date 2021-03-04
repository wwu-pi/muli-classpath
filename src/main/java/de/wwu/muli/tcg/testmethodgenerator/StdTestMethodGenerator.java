package de.wwu.muli.tcg.testmethodgenerator;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.TestCaseGenerator;
import de.wwu.muli.tcg.utility.Indentation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static de.wwu.muli.tcg.utility.Utility.*;

public class StdTestMethodGenerator implements TestMethodGenerator {
    protected final Class<?>[] specialCases;
    protected final String assertEqualsDelta;
    protected final Set<Class<?>> encounteredTypes;
    // Temporary store for objects and their argument name in the test method
    // Should be cleared after the String for the TestCase-object was generated.
    protected Map<Object, String> argumentNamesForObjects = new HashMap<>();
    // Temporary stores for object types (represented as strings) how many of them are already within a method.
    // Should be cleared after the String for the TestCase-object was generated.
    protected Map<String, Integer> argumentNameToNumberOfOccurrences = new HashMap<>();
    protected boolean isObjectMethod;
    protected String objectCalleeIdentifier;
    // Temporary stores all input objects
    protected Object[] inputObjects = null;
    // Temporary stores all output objects;
    protected Object outputObject = null;
    // An identifier for the currently generated test
    protected int numberOfTest = 0;
    // Full name of class for which test cases are generated
    protected final String fullTestedClassName;
    // Name of class for which test cases are generated
    protected final String testedClassName;
    // Name of method for which test cases are generated
    protected final String testedMethodName;
    protected Indentation indentation;
    protected final boolean assumeSetter;

    public StdTestMethodGenerator(Indentation indentation, String fullTestedClassName, String testedClassName, String testedMethodName) {
        this(indentation, fullTestedClassName, testedClassName, testedMethodName, true, "10e-6", Collection.class, Map.class);
    }

    public StdTestMethodGenerator(Indentation indentation, String fullTestedClassName, String testedClassName, String testedMethodName, boolean assumeSetter) {
        this(indentation, fullTestedClassName, testedClassName, testedMethodName, assumeSetter, "10e-6", Collection.class, Map.class);
    }

    public StdTestMethodGenerator(Indentation indentation, String fullTestedClassName, String testedClassName, String testedMethodName, boolean assumeSetter, String assertEqualsDelta, Class<?>... specialCases) {
        this.assertEqualsDelta = assertEqualsDelta;
        this.specialCases = specialCases;
        encounteredTypes = new HashSet<>();
        this.indentation = indentation;
        this.assumeSetter = assumeSetter;
        this.fullTestedClassName = fullTestedClassName;
        this.testedClassName = testedClassName;
        this.testedMethodName = testedMethodName;
    }

    @Override
    public String generateTestCaseStringRepresentation(TestCase<?> tc) {
        before(tc);
        String result = execute(tc);
        after(tc);
        return result;
    }

    protected void before(TestCase<?> tc) {
        if (!testedClassName.equals(tc.getClassName()) || !testedMethodName.equals(tc.getMethodName()) ||
                !fullTestedClassName.equals(tc.getFullClassName())) {
                throw new UnsupportedOperationException("Test cases can only be generated for one specific method " +
                        "at a time.");
        }
        isObjectMethod = tc.getInput("this") != null;
        inputObjects = tc.getInputs();
        outputObject = tc.getOutput();
        argumentNamesForObjects.put(null, "null");
    }

    protected void after(TestCase<?> tc) {
        argumentNamesForObjects = new HashMap<>();
        argumentNameToNumberOfOccurrences = new HashMap<>();
        isObjectMethod = false;
        objectCalleeIdentifier = null;
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
        sb.append(indentation.indentBlock(generateStringsForInputs(tc.getInputs())));
        sb.append(indentation.indentBlock(generateStringForOutput(tc.getOutput())));
        sb.append(indentation.indentBlock(generateAssertionString()));
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
        for (int i = 0; i < inputs.length; i++) {
            sb.append(generateElementString(inputs[i]));
            if (i == 0 && isObjectMethod) {
                objectCalleeIdentifier = argumentNamesForObjects.get(inputObjects[i]);
            }
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

        Class<?> oc = o.getClass();
        addToEncounteredTypes(oc);
        generateNumberedArgumentName(o);
        if (isPrimitiveClass(oc)) {
            return generatePrimitiveString(o);
        } else if (isWrappingClass(oc)) {
            return generateWrappingString(o);
        } else if (isStringClass(oc)) {
            return generateStringString(o);
        } else if (isArray(oc)) {
            return generateArrayString(o);
        } else if(oc.isArray()){
            if(oc.getComponentType().isArray()){
                return generateMultiArrayString(o);
            } else {
                return generateArrayString(o);
            }
        } else {
            return generateObjectString(o);
        }
    }

    protected String generateArrayString(Object o) {
        StringBuilder sb = new StringBuilder();
        String arrayName = argumentNamesForObjects.get(o);
        String type = o.getClass().getSimpleName();
        int index = type.indexOf("[");
        if(index != -1){
            type = type.substring(0, index);
        }
        sb.append(o.getClass().getSimpleName())
                .append(" ")
                .append(arrayName)
                .append(" = new ")
                .append(type)
                .append("[")
                .append(Array.getLength(o))
                .append("]")
                .append(";\r\n");

        for (int i = 0; i < Array.getLength(o); i++) {
            Object arrayElement = Array.get(o, i);
            sb.append(generateElementString(arrayElement));
            sb.append(arrayName)
                    .append("[")
                    .append(i)
                    .append("] = ")
                    .append(argumentNamesForObjects.get(arrayElement))
                    .append(";\r\n");
        }

        return sb.toString();
    }

    protected String generateMultiArrayString(Object o) {
        StringBuilder sb = new StringBuilder();
        String arrayName = argumentNamesForObjects.get(o);
        String type = o.getClass().getSimpleName();
        int index = type.indexOf("[");
        if(index != -1){
            type = type.substring(0, index);
        }
        sb.append(o.getClass().getSimpleName())
                .append(" ")
                .append(arrayName)
                .append(" = new ")
                .append(type)
                .append("[")
                .append(Array.getLength(o))
                .append("]")
                .append("[")
                .append(Array.getLength(Array.get(o, 0)))
                .append("]")
                .append(";\r\n");

        for (int i = 0; i < Array.getLength(o); i++) {
            Object arrayElement = Array.get(o, i);
            sb.append(generateElementString(arrayElement));
            sb.append(arrayName)
                    .append("[")
                    .append(i)
                    .append("] = ")
                    .append(argumentNamesForObjects.get(arrayElement))
                    .append(";\r\n");
        }

        return sb.toString();
    }

    protected void addToEncounteredTypes(Class<?> oc) {
        if (oc.isPrimitive()) {
            return;
        }
        if (oc.isArray()) {
            Class<?> componentClass = oc.getComponentType();
            if (componentClass.isPrimitive()) {
                return;
            }
            addToEncounteredTypes(componentClass);
        } else {
            encounteredTypes.add(oc);
        }
    }

    protected boolean isArray(Class<?> oc) {
        return oc.getClass().isArray();
    }

    protected boolean isAlreadyCreated(Object o) {
        return argumentNamesForObjects.containsKey(o);
    }

    protected String generatePrimitiveString(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.getClass().getSimpleName());
        sb.append(argumentNamesForObjects.get(o));
        sb.append(" = ").append(o);
        return sb.toString();
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
        if (type.isArray()) {
            if(type.getComponentType().isArray()) {
                String simpleName = type.getSimpleName();
                return toFirstLower(simpleName.substring(0, simpleName.length() - 4)) + "Ar";
            } else {
                String simpleName = type.getSimpleName();
                return toFirstLower(simpleName.substring(0, simpleName.length() - 2)) + "Ar";
            }
        } else {
            return toFirstLower(type.getSimpleName());
        }
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
            if (skipField(f)) {
                continue;
            }
            sb.append(generateSetStatementForObject(objectArgumentName, o, f));
        }
        return sb.toString();
    }

    protected boolean skipField(Field f) {
        return f.getName().contains("jacoco");
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
            if (assumeSetter) {
                sb.append(objectArgumentName)
                        .append(".set")
                        .append(toFirstUpper(fieldName))
                        .append("(")
                        .append(fieldValueArgumentName)
                        .append(");\r\n");
            } else {
                sb.append(TestCaseGenerator.REFLECTION_SETTER_METHOD_NAME)
                        .append("(")
                        .append(objectArgumentName)
                        .append(", \"")
                        .append(f.getName())
                        .append("\", ")
                        .append(fieldValueArgumentName)
                        .append(");\r\n");
            }
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
        if (outputObject != null && !outputObject.getClass().isArray()) {
            sb.append("assertEquals(").append(outputObjectName).append(", ");
        } else if (outputObject != null && outputObject.getClass().isArray()) {
            sb.append("assertArrayEquals(").append(outputObjectName).append(", ");
        }
        sb.append(generateTestedMethodCallString(inputObjectNames));
        if (outputObject != null) {
            if (isFloatingPointClass(outputObject.getClass())) {
                sb.append(", ").append(assertEqualsDelta);
            }
            sb.append(")");
        }
        sb.append(";\r\n");
        return sb.toString();
    }

    protected String generateTestedMethodCallString(String[] inputObjectNames) {
        StringBuilder sb = new StringBuilder();
        if (isObjectMethod) {
            sb.append(objectCalleeIdentifier);
        } else {
            sb.append(fullTestedClassName);
        }
        sb.append(".").append(testedMethodName).append("(");
        for (int i = 0; i < inputObjectNames.length; i++) {
            if (i == 0 && isObjectMethod) {
                continue;
            }
            sb.append(inputObjectNames[i]).append(",");
        }
        if (inputObjectNames.length - (isObjectMethod ? 1 : 0) != 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
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
