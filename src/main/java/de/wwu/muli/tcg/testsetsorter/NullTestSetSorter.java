package de.wwu.muli.tcg.testsetsorter;

import de.wwu.muli.solution.TestCase;

import java.util.Comparator;

public class NullTestSetSorter extends TestSetSorter {

    public NullTestSetSorter() {
        super(new NullTestCaseComparator());
    }

    protected static class NullTestCaseComparator implements Comparator<TestCase<?>> {
        @Override
        public int compare(TestCase<?> o1, TestCase<?> o2) {
            return o1.getTestNumber() < o2.getTestNumber() ? -1 : 1;
        }
    }

}
