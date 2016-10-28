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
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        return search(b, SearchStrategy.IterativeDeepening, searchArea);
    }

    public native static Solution muli(Supplier searchArea);

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
