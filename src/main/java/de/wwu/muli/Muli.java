package de.wwu.muli;

import java.util.Arrays;
import java.util.function.Supplier;

public class Muli {

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] search(Find b, SearchStrategy c, Supplier<T> searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        return Muli.muli(searchArea);
        // TODO make Find preference available to VM!
        // TODO make SearchStrategy preference available to VM!
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] search(Find b, Supplier<T> searchArea) {
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static <T> Solution<T>[] muli(Supplier<T> searchArea) {
        ExecutionMode previousMode = getVMExecutionMode(); // Locally record previous mode of VM
        setVMExecutionMode(ExecutionMode.SYMBOLIC);

        try {
            Object retval = searchArea.get();
            recordSolutionAndBacktrackVM(retval);
        } catch(Throwable e) {
            // This happens if searchArea.get() threw an exception.
            // However, we consider exceptions as part of the solution.
            recordExceptionAndBacktrackVM(e);
        }
        setVMExecutionMode(previousMode); // Restore previous mode of VM

        return getVMRecordedSolutions();
    }

    public static <T> T getOneValue(Supplier<T> searchArea) {
        Solution<T>[] search = Muli.<T>search(Find.First, searchArea);
        return (T)Arrays.stream(search)
                .filter(x -> !x.isExceptionControlFlow)
                .findFirst()
                .get()
                .value;
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native MuliFailException fail(); // Can't be declared as "throws MuliFailE..." because compiler does not recognise it as invariant

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native void label();

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    private static native void recordSolutionAndBacktrackVM(Object solution);
    private static native void recordExceptionAndBacktrackVM(Throwable exception);

    private static native <T> Solution<T>[] getVMRecordedSolutions();

    // TODO: maybe add intermediate type representing the (continuable) search space
}
