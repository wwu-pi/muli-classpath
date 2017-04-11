package de.wwu.muli;

import java.util.function.Supplier;

public class Muli {

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static Solution[] search(Find b, SearchStrategy c, Supplier searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        return Muli.muli(searchArea);
        // TODO make Find preference available to VM!
        // TODO make SearchStrategy preference available to VM!
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static Solution[] search(Find b, Supplier searchArea) {
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static Solution[] muli(Supplier searchArea) {
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

    @SuppressWarnings({"WeakerAccess", "unused"}) // Public API
    public static native MuliFailException fail();

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    private static native void recordSolutionAndBacktrackVM(Object solution);
    private static native void recordExceptionAndBacktrackVM(Throwable exception);

    private static native Solution[] getVMRecordedSolutions();

    // TODO: maybe add intermediate type representing the (continuable) search space
}
