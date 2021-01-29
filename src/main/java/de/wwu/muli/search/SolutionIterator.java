package de.wwu.muli.search;

import de.wwu.muli.ExecutionMode;
import de.wwu.muli.Muli;
import de.wwu.muli.solution.Solution;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SolutionIterator<T> implements Spliterator<Solution<T>> {
    private final Supplier<T> searchRegion;
    private final String methodToTest;
    private final Boolean generateTestCase;

    public SolutionIterator(Supplier<T> searchRegion) {
        this(searchRegion, false, null);
    }

    public SolutionIterator(Supplier<T> searchRegion, Boolean generateTestCase, String methodToTest) {
        this.searchRegion = searchRegion;
        this.generateTestCase = generateTestCase;
        if (generateTestCase) {
            if (methodToTest == null
                    || methodToTest.length() == 0
                    || !methodToTest.contains(".")) {
                throw new IllegalArgumentException("If test cases are to be generated, a fully qualified method name must be specified.");
            }
        }
        this.methodToTest = methodToTest;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Solution<T>> consumer) {
        //if (!choicePointHasAdditionalChoiceVM(this)) {
        //    return false;
        //}

        ExecutionMode previousMode = Muli.getVMExecutionMode(); // Locally record previous mode of VM (for nested search regions).
        Muli.setVMExecutionMode(ExecutionMode.SYMBOLIC);
        SolutionIterator previousIterator = getVMActiveIterator(); // Locally record previously active iterator of VM (for nested search regions).
        setVMActiveIterator(this);

        if (!replayInverseTrailForNextChoiceVM()) {
            // Remaining choice points did not actually have additional choices, i.e. their
            // branch conditions were unsatisfiable given the constraint stack.
            Muli.setVMExecutionMode(previousMode); // Restore previous mode of VM.
            setVMActiveIterator(previousIterator); // Make previous iterator active (if any).
            return false;
        }

        Solution<T> oneSolution;
        try {
            Object retval = this.searchRegion.get();
            if (retval instanceof NoFurtherSolutionsIndicator) {
                // We tried, but there is no additional solution.
                Muli.setVMExecutionMode(previousMode); // Restore previous mode of VM.
                setVMActiveIterator(previousIterator); // Make previous iterator active (if any).
                return false;
            }
            oneSolution = (Solution<T>) wrapSolutionAndFullyBacktrackVM(retval, generateTestCase, methodToTest);

        } catch (Throwable e) {
            // This happens if searchRegion.get() threw an exception.restoreChoicePointStateNextChoiceVM
            // However, we consider exceptions as part of the solution.
            oneSolution = (Solution<T>) wrapExceptionAndFullyBacktrackVM(e, generateTestCase, methodToTest);
        }

        Muli.setVMExecutionMode(previousMode); // Restore previous mode of VM.
        setVMActiveIterator(previousIterator); // Make previous iterator active (if any).

        consumer.accept(oneSolution);
        return true;
    }

    private static native boolean choicePointHasAdditionalChoiceVM(SolutionIterator<?> it);

    /**
     * For continued search regions, this replays operations from the inverse trail, thus rebuilding the former state and the former trail.
     * For fresh search regions, this marks a "root choice point" instead, to which backtracking will always occur.
     * */
    private static native boolean replayInverseTrailForNextChoiceVM();

    private static native Solution<?> wrapSolutionAndFullyBacktrackVM(Object solution, Boolean generateTestCase, String methodToTest);
    private static native Solution<?> wrapExceptionAndFullyBacktrackVM(Throwable exception, Boolean generateTestCase, String methodToTest);

    // Active search region / corresponding iterator.
    public static native SolutionIterator getVMActiveIterator();
    public static native void setVMActiveIterator(SolutionIterator mode);

    @Override
    public Spliterator<Solution<T>> trySplit() {
        // From the Spliterator documentation: "Returns a Spliterator covering some portion of the elements,
        // or null if this spliterator cannot be split."
        // We don't know how to split something non-deterministic, therefore we return null.
        return null;
    }

    @Override
    public long estimateSize() {
        // We have no idea. If that is the case, Long.MAX_VALUE is the intended default.
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL | Spliterator.IMMUTABLE;
    }
}