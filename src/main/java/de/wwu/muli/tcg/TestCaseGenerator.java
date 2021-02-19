package de.wwu.muli.tcg;

import de.wwu.muli.solution.Solution;
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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class TestCaseGenerator {

    public static final String REFLECTION_SETTER_METHOD_NAME = "setWithReflection";
    protected final Indentation indentation;
    protected TestClassGenerator testClassGenerator;
    protected TestMethodGenerator testMethodGenerator;
    protected TestSetReducer testSetReducer;
    protected TestSetSorter testSetSorter;
    protected final Set<TestCase<?>> testCases;
    protected final String fullTestedClassName;
    protected final String testedMethodName;
    protected final String testedClassName;
    protected final String packageName;
    protected final PrintStream printer;

    private TestCaseGenerator(
            String packageName, String fullTestedClassName,
            String testedClassName, String testedMethodName,
            Set<TestCase<?>> testCases,
            PrintStream printer) {
        this.packageName = packageName;
        this.fullTestedClassName = fullTestedClassName;
        this.testedClassName = testedClassName;
        this.testedMethodName = testedMethodName;
        this.testCases = Collections.unmodifiableSet(testCases);
        indentation = Indentation.withTab();
        testMethodGenerator = new StdTestMethodGenerator(indentation, fullTestedClassName, testedClassName, testedMethodName);
        testClassGenerator = new StdTestClassGenerator(indentation);
        this.printer = printer;
    }

    public static TestCaseGenerator get(Solution[] solutions) {
        return get(solutions, System.out);
    }

    public static TestCaseGenerator get(Solution[] solutions, PrintStream printer) {
        String testedMethodName = null;
        String testedClassName = null;
        String packageName = null;
        String fullTestedClassName = null;
        Set<TestCase<?>> testCases = new HashSet<>();
        for (Solution s : solutions) {
            TestCase<?> tc = s.testCase;
            if (packageName == null) {
                packageName = tc.getFullClassName().substring(0, tc.getFullClassName().lastIndexOf('.'));
                testedClassName = tc.getClassName();
                testedMethodName = tc.getMethodName();
                fullTestedClassName = tc.getFullClassName();
            }
            testCases.add(tc);
        }
        if (packageName == null || fullTestedClassName == null || testedClassName == null || testedMethodName == null) {
            throw new IllegalStateException("Missing data for test case.");
        }

        return new TestCaseGenerator(packageName, fullTestedClassName, testedClassName, testedMethodName, testCases, printer);
    }

    public String generateTestClassStringRepresentation() {
        String testedClassName = testMethodGenerator.getTestedClassName();
        String testedMethodName = testMethodGenerator.getTestedMethodName();
        if ((testedClassName == null || testedClassName.length() == 0)||
                (testedMethodName == null || testedMethodName.length() == 0)) {
            throw new RuntimeException("Name of the method or class for which tests are generated is not given.");
        }

        Set<TestCase<?>> tests = testCases;
        tests = reduceTestCases(tests);
        SortedSet<TestCase<?>> sortedTests = sortTestCases(tests);
        List<String> stringsForTests = generateStringRepresentation(sortedTests);

        String result = testClassGenerator.generateTestClassString(
                packageName,
                testedClassName,
                testMethodGenerator.getEncounteredTypes(),
                stringsForTests
        );
        printer.println(result);
        return result;
    }

    protected List<String> generateStringRepresentation(SortedSet<TestCase<?>> testCases) {
        List<String> stringsForTests = new ArrayList<>();
        for (TestCase<?> tc : testCases) {
            stringsForTests.add(testMethodGenerator.generateTestCaseStringRepresentation(tc));
        }
        return stringsForTests;
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
