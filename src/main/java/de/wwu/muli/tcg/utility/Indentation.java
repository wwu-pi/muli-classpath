package de.wwu.muli.tcg.utility;

public class Indentation {

    protected final String whiteSpace;

    protected Indentation() {
        this("\t");
    }

    protected Indentation(String whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    public String identLine(String line) {
        if (line == null || line.length() == 0) {
            return line;
        } else {
            return whiteSpace + line;
        }
    }

    public String indentBlock(String block) {
        if (block == null || block.length() == 0) {
            return block;
        } else {
            return block.replaceAll("(?m)^", whiteSpace);
        }
    }

    public static Indentation withTab() {
        return new Indentation("\t");
    }

    public static Indentation withSpaces() {
        return withSpaces("   ");
    }

    public static Indentation withSpaces(String spaces) {
        if (spaces == null) {
            throw new IllegalArgumentException("Spaces must not be null");
        }
        for (char ch : spaces.toCharArray()) {
            if (ch != ' ') {
                throw new IllegalArgumentException("Only spaces are allowed here.");
            }
        }
        return new Indentation(spaces);
    }
}
