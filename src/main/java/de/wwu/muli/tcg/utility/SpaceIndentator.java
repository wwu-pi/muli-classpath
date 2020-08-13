package de.wwu.muli.tcg.utility;

public class SpaceIndentator implements Indentator {

    private final String spaces;
    private static final int DEFAULT_NUMBER_SPACES = 3;

    public SpaceIndentator() {
        this(DEFAULT_NUMBER_SPACES);
    }

    public SpaceIndentator(int numberSpaces) {
        StringBuilder spacesString = new StringBuilder();
        for (int i = 0; i < numberSpaces; i++) {
            spacesString.append(" ");
        }
        spaces = spacesString.toString();
    }

    @Override
    public String identLine(String line) {
        if (line == null || line.length() == 0) {
            return line;
        } else {
            return spaces + line;
        }
    }

    @Override
    public String indentBlock(String block) {
        if (block == null || block.length() == 0) {
            return block;
        } else {
            return block.replaceAll("(?m)^", spaces);
        }
    }
}
