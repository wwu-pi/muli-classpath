package de.wwu.muli.tcg.testclassgenerator;

import java.util.List;
import java.util.Set;

public interface TestClassGenerator {

    String generateTestClassString(String packageName,
                                   String testedClassName,
                                   Set<Class<?>> encounteredTypes,
                                   List<String> testMethodStrings);

}
