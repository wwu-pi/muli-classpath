package de.wwu.muli;

import java.util.function.Supplier;

public class Muli {

    public static native MuliFailException fail();

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
        ExecutionMode previousMode = getVMExecutionMode(); // Locally record previous state of VM
        setVMExecutionMode(ExecutionMode.SYMBOLIC);
        setVMSymbolicExecutionTreeRoot(); // Record start of symbolic execution (translates into a choice point)
        try {
            Object retval = searchArea.get(); // Maybe transfer call to VM? this should be easy
            recordSolutionAndBacktrackVM(retval);
        } catch(Throwable e) {
            recordSolutionAndBacktrackVM(e);
        }
        setVMExecutionMode(previousMode); // Restore previous state of VM



        return getVMRecordedSolutions();
    }

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
