package de.wwu.muli.tcg.testclassgenerator;

import de.wwu.muli.tcg.utility.Indentation;

import java.util.*;

public class StdTestClassGenerator implements TestClassGenerator {

    protected final Indentation indentation;

    public StdTestClassGenerator(Indentation indentation) {
        this.indentation = indentation;
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
