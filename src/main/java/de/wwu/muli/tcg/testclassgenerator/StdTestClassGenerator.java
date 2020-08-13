package de.wwu.muli.tcg.testclassgenerator;

import de.wwu.muli.tcg.utility.Indentator;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class StdTestClassGenerator implements TestClassGenerator {

    protected final Indentator indentator;

    public StdTestClassGenerator(Indentator indentator) {
        this.indentator = indentator;
    }

    @Override
    public String generateTestClassString(String packageName,
                                          String testedClassName,
                                          Set<Class<?>> encounteredTypes,
                                          SortedSet<String> testMethodStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatePackageDeclaration(packageName));
        sb.append(generateImports(encounteredTypes));
        sb.append(generateTestClassAnnotations());
        sb.append(generateTestClassDeclaration(testedClassName));
        sb.append(generateClassAttributes());
        sb.append(indentator.indentBlock(generateBeforeClassMethod()));
        sb.append(indentator.indentBlock(generateAfterClassMethod()));
        sb.append(indentator.indentBlock(generateBeforeMethod()));
        sb.append(indentator.indentBlock(generateAfterMethod()));

        for (String testMethodString : testMethodStrings) {
            sb.append(indentator.indentBlock(testMethodString)).append("\r\n");
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
            sb.append("import ").append(typeToImport.getName()).append(";\r\n");
        }
        sb.append("\r\n\r\n");
        return sb.toString();
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
