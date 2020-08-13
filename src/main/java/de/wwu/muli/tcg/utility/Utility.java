package de.wwu.muli.tcg.utility;

public class Utility {
    private Utility() {}

    public static String toFirstLower(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String toFirstUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
