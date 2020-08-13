package de.wwu.muli.tcg;

import de.wwu.muli.solution.TestCase;
import de.wwu.muli.tcg.testclassgenerator.TestClassGenerator;
import de.wwu.muli.tcg.testmethodgenerator.TestMethodGenerator;
import de.wwu.muli.tcg.testsetreducer.NullTestSetReducer;
import de.wwu.muli.tcg.testsetreducer.TestSetReducer;
import de.wwu.muli.tcg.testsetsorter.NullTestSetSorter;
import de.wwu.muli.tcg.testsetsorter.TestSetSorter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public class TestCaseGeneratorManager {

    private static TestCaseGeneratorManager instance;
    protected TestClassGenerator testClassGenerator;
    protected TestMethodGenerator testMethodGenerator;
    protected TestSetReducer testSetReducer;
    protected TestSetSorter testSetSorter;
    protected Map<TestCase<?>, String> testCasesToString = new HashMap<>();

    private TestCaseGeneratorManager() {}

    public static final TestCaseGeneratorManager getInstance() {
        if (instance == null) {
            instance = new TestCaseGeneratorManager();
        }
        return instance;
    }

    public String generateTestClassStringRepresentation() {
        return ""; // TODO
    }

    public void acceptNewTestCase(TestCase<?> tc) {
        testCasesToString.put(tc, testMethodGenerator.generateTestCaseStringRepresentation(tc));
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
