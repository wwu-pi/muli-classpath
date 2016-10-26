package de.wwu.muli;

import java.util.function.Supplier;

public class Muli {

    public static native void fail();

    public static Solution[] search(Find b, SearchStrategy c, Supplier<Boolean> searchArea) {
        // TODO: iterate through multiple results of searchArea.get() == true => not anymore!
        // TODO: maybe this is only a stub? or is the inner one a stub? ... hm
        return new Solution[]{Muli.muli(searchArea)};
    }

    public native static Solution muli(Supplier<Boolean> searchArea);

    // TODO: maybe add intermediate type representing the (continuable) search space
    //public static Solution[] muli(int labelling, Supplier<Boolean> searchArea) {
    //	return null;
    //}
}
