package de.wwu.muli.tcg.testclassgenerator;

import java.util.Set;
import java.util.SortedSet;

public class StdTestClassGenerator implements TestClassGenerator {

    @Override
    public String generateTestClassString(String packageName,
                                          String testedClassName,
                                          Set<Class<?>> encounteredTypes,
                                          SortedSet<String> testMethodStrings) {
        StringBuilder sb = new StringBuilder();
        sb.append(generatePackageDeclaration(packageName));
        sb.append(generateImports(encounteredTypes));
        sb.append(generateTestClassAnnotations());
        sb.append(generateTestClassDeclaration());
        sb.append(generateBeforeClassMethod());
        sb.append(generateAfterClassMethod());
        sb.append(generateBeforeMethod());
        sb.append(generateAfterMethod());

        for (String testMethodString : testMethodStrings) {
            sb.append(testMethodString).append("\r\n");
        }

        sb.append(generateClassEnd());

        return sb.toString();
    }

    protected String generatePackageDeclaration(String packageName) {
        return "package " + packageName + ";\r\n";
    }

    protected String generateImports(Set<Class<?>> encounteredTypes) {
        StringBuilder sb = new StringBuilder();

        for (Class<?> typeToImport : encounteredTypes) {
            sb.append("import ").append(typeToImport.getName()).append(";\r\n");
        }

        return sb.toString();
    }

    protected String generateTestClassAnnotations() {
        return ""; // TODO
    }

    protected String generateTestClassDeclaration() {
        return ""; // TODO
    }

    protected String generateBeforeClassMethod() {
        return ""; // TODO
    }

    protected String generateAfterClassMethod() {
        return ""; // TODO
    }

    protected String generateBeforeMethod() {
        return ""; // TODO
    }

    protected String generateAfterMethod() {
        return ""; // TODO
    }

    protected String generateClassEnd() {
        return ""; // TODO
    }

}
