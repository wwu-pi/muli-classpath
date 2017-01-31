package de.wwu.muli;

import java.util.function.Supplier;

public class Muli {

    public static native MuliFailException fail();

    public static Solution[] search(Find b, SearchStrategy c, Supplier searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        Solution[] solutions = Muli.muli(searchArea);
        if (b == Find.First) {
            return new Solution[]{solutions[0]};
        } else {
            return solutions;
        }
    }

    public static Solution[] search(Find b, Supplier searchArea) {
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    public static Solution[] muli(Supplier searchArea) {
        // Find out prior VM state in order to reset it after executing the current search area
        ExecutionMode previousMode = getVMExecutionMode();
        int previousRecursionLevel = getVMRecursionLevel();

        // Execute search area symbolically
        setVMExecutionMode(ExecutionMode.SYMBOLIC);
        setVMRecursionLevel(previousRecursionLevel + 1);
        Object retval = searchArea.get(); // Maybe transfer call to VM? this should be easy
        Solution[] solutions = getVMSolutions(getVMRecursionLevel());

        // Return to previous VM state
        setVMRecursionLevel(previousRecursionLevel);
        setVMExecutionMode(previousMode);

        return solutions;
        // consider multiple executions of get() in case of backtracking!
        // --> no, this is implicit. As long as there are choice points in `searchArea', all their possible choices are evaluated
    }

    private static native Solution[] getVMSolutions(int level);

    private static native int getVMRecursionLevel();
    private static native void setVMRecursionLevel(int level);

    private static native ExecutionMode getVMExecutionMode();
    private static native void setVMExecutionMode(ExecutionMode mode);

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
