package de.wwu.muli;

import de.wwu.muli.search.SolutionIterator;
import de.wwu.muli.solution.MuliFailException;
import de.wwu.muli.solution.Solution;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Muli {

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> search(Find b, SearchStrategy c, Supplier<T> searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        return Muli.muli(searchArea);
        // TODO make Find preference available to VM!
        // TODO make SearchStrategy preference available to VM!
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> search(Find b, Supplier<T> searchArea) {
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Stream<Solution<T>> muli(Supplier<T> searchArea) {
        //ExecutionMode previousMode = getVMExecutionMode(); // Locally record previous mode of VM
        //setVMExecutionMode(ExecutionMode.SYMBOLIC);

        return StreamSupport.stream(new SolutionIterator<T>(searchArea), false);

        //try {
        //    Object retval = searchArea.get();
        //    recordSolutionAndBacktrackVM(retval);
        //} catch(Throwable e) {
            // This happens if searchArea.get() threw an exception.
            // However, we consider exceptions as part of the solution.
        //    recordExceptionAndBacktrackVM(e);
        //}
        //setVMExecutionMode(previousMode); // Restore previous mode of VM

        //return Arrays.stream(getVMRecordedSolutions());
    }

    public static <T> T getOneValue(Supplier<T> searchArea) {
        Stream<Solution<T>> search = Muli.<T>search(Find.First, searchArea);
        return search
                .filter(x -> !x.isExceptionControlFlow())
                .findFirst()
                .get()
                .value;
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native MuliFailException fail(); // Can't be declared as "throws MuliFailE..." because compiler does not recognise it as invariant

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native void label();

    public static native ExecutionMode getVMExecutionMode();
    public static native void setVMExecutionMode(ExecutionMode mode);

    private static native void recordSolutionAndBacktrackVM(Object solution);
    private static native void recordExceptionAndBacktrackVM(Throwable exception);

    private static native <T> Solution<T>[] getVMRecordedSolutions();
}
