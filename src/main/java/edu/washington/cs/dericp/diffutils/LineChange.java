package edu.washington.cs.dericp.diffutils;

/**
 * This class represents a single change in a patch. This can either be the
 * deletion or insertion of a line.
 */
public interface LineChange {

    public enum Type {
        INSERTION, DELETION, CONTEXT
    }

    int getOriginalLineNumber();
    int getRevisedLineNumber();
    String getLine();
    Type getType();
}
