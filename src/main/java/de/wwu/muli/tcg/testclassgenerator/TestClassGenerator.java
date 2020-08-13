package de.wwu.muli.tcg.testclassgenerator;

import java.util.Set;
import java.util.SortedSet;

public interface TestClassGenerator {

    String generateTestClassString(String packageName,
                                   String testedClassName,
                                   Set<Class<?>> encounteredTypes,
                                   SortedSet<String> testMethodStrings);

}
