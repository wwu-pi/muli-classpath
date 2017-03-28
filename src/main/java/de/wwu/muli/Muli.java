package de.wwu.muli;

import java.util.function.Supplier;

public class Muli {

    public static Solution[] search(Find b, SearchStrategy c, Supplier searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        if (b == Find.First) {
            return new Solution[]{Muli.muli(searchArea)};
        } else {
            throw new IllegalArgumentException("not supported yet. :(");
        }
    }

    public static Solution[] search(Find b, Supplier searchArea) {
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    public static Solution muli(Supplier searchArea) {
        ExecutionMode previousMode = getVMExecutionMode(); // Locally record previous mode of VM
        setVMExecutionMode(ExecutionMode.SYMBOLIC);

        try {
            Object retval = searchArea.get(); // Maybe transfer call to VM? this should be easy
            recordSolutionAndBacktrackVM(retval);
        } catch(Throwable e) {
            recordExceptionAndBacktrackVM(e);
        }
        setVMExecutionMode(previousMode); // Restore previous mode of VM

        return getVMRecordedSolutions(); // TODO -> array.
    }

    public static native MuliFailException fail();

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    private static native void setVMSymbolicExecutionTreeRoot();
    private static native void recordSolutionAndBacktrackVM(Object solution);
    private static native void recordExceptionAndBacktrackVM(Throwable exception);

    private static native Solution getVMRecordedSolutions();

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
