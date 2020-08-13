package de.wwu.muli.tcg.utility;

public class TabIndentator implements Indentator {

    @Override
    public String identLine(String line) {
        if (line == null || line.length() == 0) {
            return line;
        } else {
            return "\t" + line;
        }
    }

    @Override
    public String indentBlock(String block) {
        if (block == null || block.length() == 0) {
            return block;
        } else {
            return block.replaceAll("(?m)^", "\t");
        }
    }
}
