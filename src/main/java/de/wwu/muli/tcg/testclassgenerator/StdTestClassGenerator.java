package de.wwu.muli.tcg.testclassgenerator;

import de.wwu.muli.tcg.TestCaseGenerator;
import de.wwu.muli.tcg.utility.Indentation;

import java.util.*;

public class StdTestClassGenerator implements TestClassGenerator {

    protected final Indentation indentation;
    protected boolean assumeSetter;

    public StdTestClassGenerator(Indentation indentation) {
        this(indentation, true);
    }

    public StdTestClassGenerator(Indentation indentation, boolean assumeSetter) {
        this.indentation = indentation;
        this.assumeSetter = assumeSetter;
    }

    @Override
    public String generateTestClassString(String packageName,
                                          String testedClassName,
                                          Set<Class<?>> encounteredTypes,
                                          List<String> testMethodStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatePackageDeclaration(packageName));
        sb.append(generateImports(encounteredTypes));
        sb.append(generateTestClassAnnotations());
        sb.append(generateTestClassDeclaration(testedClassName));
        sb.append(generateClassAttributes());
        sb.append(indentation.indentBlock(generateUtilityMethods()));
        sb.append(indentation.indentBlock(generateBeforeClassMethod()));
        sb.append(indentation.indentBlock(generateAfterClassMethod()));
        sb.append(indentation.indentBlock(generateBeforeMethod()));
        sb.append(indentation.indentBlock(generateAfterMethod()));

        for (String testMethodString : testMethodStrings) {
            sb.append(indentation.indentBlock(testMethodString)).append("\r\n");
        }

        sb.append(generateClassEnd());

        return sb.toString();
    }

    protected String generatePackageDeclaration(String packageName) {
        return "package " + packageName + ";\r\n";
    }

    protected String generateImports(Set<Class<?>> encounteredTypes) {
        StringBuilder sb = new StringBuilder();

        sb.append("import org.junit.*;\r\n");
        sb.append("import static org.junit.Assert.*;\r\n");

        encounteredTypes = sortEncounteredTypes(encounteredTypes);

        for (Class<?> typeToImport : encounteredTypes) {
            if (!omitFromImport(typeToImport)) {
                sb.append("import ").append(typeToImport.getName()).append(";\r\n");
            }
        }
        sb.append("\r\n\r\n");
        return sb.toString();
    }

    protected boolean omitFromImport(Class<?> type) {
        return type.getName().startsWith("java.lang.");
    }

    protected SortedSet<Class<?>> sortEncounteredTypes(Set<Class<?>> encounteredTypes) {
        SortedSet<Class<?>> result = new TreeSet<>(new AlphabeticalTypeComparator());
        result.addAll(encounteredTypes);
        return result;
    }

    protected static class AlphabeticalTypeComparator implements Comparator<Class<?>> {

        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    protected String generateTestClassAnnotations() {
        return "@SuppressWarnings(\"all\")\r\n";
    }

    protected String generateTestClassDeclaration(String testedClassName) {
        return "public class Test" + testedClassName + " {\r\n";
    }

    protected String generateClassAttributes() {
        return "";
    }

    protected String generateUtilityMethods() {
        if (assumeSetter) {
            return "";
        } else { // Utility method to use reflection instead of setters to set an object's field.
            return "protected void " + TestCaseGenerator.REFLECTION_SETTER_METHOD_NAME +
                        "(Object setFor, String fieldName, Object setTo) {\r\n" +
                    indentation.indentBlock(
                            "if (fieldName.startsWith(\"this$\")) {\r\n" +
                            indentation.indentLine("return;\r\n") +
                            "}\r\n" +
                            "try { \r\n" +
                            indentation.indentBlock(
                                    "Class<?> setForClass = setFor.getClass();\r\n" +
                                    "Field setForField = setForClass.getDeclaredField(fieldName);\r\n" +
                                    "boolean accessible = setForField.isAccessible();\r\n" +
                                    "setForField.setAccessible(true);\r\n" +
                                    "setForField.set(setFor, setTo);\r\n" +
                                    "setForField.setAccessible(accessible);\r\n") +
                            "} catch (Exception e) {\r\n" +
                            indentation.indentLine("throw new RuntimeException(e);\r\n") +
                            "}\r\n") +
                    "}\r\n";
        }
    }

    protected String generateBeforeClassMethod() {
        return "";
    }

    protected String generateAfterClassMethod() {
        return "";
    }

    protected String generateBeforeMethod() {
        return "";
    }

    protected String generateAfterMethod() {
        return "";
    }

    protected String generateClassEnd() {
        return "}\r\n";
    }
}
