package de.wwu.muli.tcg.testmethodgenerator;

import de.wwu.muli.solution.TestCase;

import java.util.Set;

public interface TestMethodGenerator {

    String generateTestCaseStringRepresentation(TestCase<?> tc);

    Set<Class<?>> getEncounteredTypes();

    String getTestedClassName();

    String getTestedMethodName();

}
