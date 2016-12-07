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
        ExecutionMode previousMode = getVMExecutionMode();
        setVMExecutionMode(ExecutionMode.SYMBOLIC);
        Object retval = searchArea.get(); // Maybe transfer call to VM? this should be easy
        setVMExecutionMode(previousMode);
        // TODO use retval or maybe get solutions from VM?
        // TODO consider multiple executions of get() in case of backtracking!
        return new Solution();
    }

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
