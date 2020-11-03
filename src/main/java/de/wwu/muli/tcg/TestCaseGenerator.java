package de.wwu.muli.tcg;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.testclassgenerator.StdTestClassGenerator;
import de.wwu.muli.tcg.testclassgenerator.TestClassGenerator;
import de.wwu.muli.tcg.testmethodgenerator.StdTestMethodGenerator;
import de.wwu.muli.tcg.testmethodgenerator.TestMethodGenerator;
import de.wwu.muli.tcg.testsetreducer.NullTestSetReducer;
import de.wwu.muli.tcg.testsetreducer.TestSetReducer;
import de.wwu.muli.tcg.testsetsorter.NullTestSetSorter;
import de.wwu.muli.tcg.testsetsorter.TestSetSorter;
import de.wwu.muli.tcg.utility.Indentation;

import java.util.*;

public class TestCaseGenerator {

    public static final String REFLECTION_SETTER_METHOD_NAME = "setWithReflection";
    private static TestCaseGenerator instance;
    protected final Indentation indentation;
    protected TestClassGenerator testClassGenerator;
    protected TestMethodGenerator testMethodGenerator;
    protected TestSetReducer testSetReducer;
    protected TestSetSorter testSetSorter;
    protected Map<TestCase<?>, String> testCasesToString = new HashMap<>();
    protected String packageName = "test"; // TODO

    private TestCaseGenerator() {
        indentation = Indentation.withTab();
        testMethodGenerator = new StdTestMethodGenerator(indentation);
        testClassGenerator = new StdTestClassGenerator(indentation);

    }

    public static final TestCaseGenerator getInstance() {
        if (instance == null) {
            instance = new TestCaseGenerator();
        }
        return instance;
    }

    public String generateTestClassStringRepresentation() {
        String testedClassName = testMethodGenerator.getTestedClassName();
        String testedMethodName = testMethodGenerator.getTestedMethodName();
        if ((testedClassName == null || testedClassName.length() == 0)||
                (testedMethodName == null || testedMethodName.length() == 0)) {
            throw new RuntimeException("Name of the method or class for which tests are generated is not given.");
        }

        Set<TestCase<?>> tests = testCasesToString.keySet();
        tests = reduceTestCases(tests);
        // TODO Sort before or after?
        SortedSet<TestCase<?>> sortedTests = sortTestCases(tests);

        List<String> stringsForTests = new ArrayList<>();

        for (TestCase<?> tc : sortedTests) {
            stringsForTests.add(testCasesToString.get(tc));
        }

        return testClassGenerator.generateTestClassString(
                packageName,
                testedClassName,
                testMethodGenerator.getEncounteredTypes(),
                stringsForTests
        );
    }

    public void acceptNewTestCase(TestCase<?> tc) {
        testCasesToString.put(tc, testMethodGenerator.generateTestCaseStringRepresentation(tc)); // TODO Should be generated in the end (after reduction), not directly!
    }

    protected Set<TestCase<?>> reduceTestCases(Set<TestCase<?>> testCases) {
        if (testSetReducer == null) {
            testSetReducer = new NullTestSetReducer();
        }
        return testSetReducer.reduceTestSet(testCases);
    }

    protected SortedSet<TestCase<?>> sortTestCases(Set<TestCase<?>> testCases) {
        if (testSetSorter == null) {
            testSetSorter = new NullTestSetSorter();
        }
        return testSetSorter.sortTestCases(testCases);
    }
}
