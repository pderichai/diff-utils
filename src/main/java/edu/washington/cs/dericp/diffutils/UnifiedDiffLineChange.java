package edu.washington.cs.dericp.diffutils;

/**
 * Created by dpang on 2/29/16.
 */
public class UnifiedDiffLineChange implements LineChange {

    private String line;
    private int originalLineNumber;
    private int revisedLineNumber;
    private Type type;

    public UnifiedDiffLineChange(String line, int originalLineNumber, int revisedLineNumber, Type type) {
        this.line = line;
        this.originalLineNumber = originalLineNumber;
        this.revisedLineNumber = revisedLineNumber;
        this.type = type;
    }

    @Override
    public String getLine() {
        return line;
    }

    @Override
    public int getOriginalLineNumber() {
        return originalLineNumber;
    }

    @Override
    public int getRevisedLineNumber() {
        return revisedLineNumber;
    }

    @Override
    public Type getType() {
        return type;
    }
}
