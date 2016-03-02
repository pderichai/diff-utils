package edu.washington.cs.dericp.diffutils.change;

/**
 * This class represents a single change in a patch. This can either be the
 * deletion or insertion of a line.
 */
public class LineChange {

    public enum Type {
        INSERTION, DELETION, CONTEXT
    }

    private String content;
    private int originalLineNumber;
    private int revisedLineNumber;
    private Type type;

    public LineChange(String content, int originalLineNumber, int revisedLineNumber, Type type) {
        this.content = content;
        this.originalLineNumber = originalLineNumber;
        this.revisedLineNumber = revisedLineNumber;
        this.type = type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getOriginalLineNumber() {
        return originalLineNumber;
    }

    public int getRevisedLineNumber() {
        return revisedLineNumber;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LineChange)) {
            return false;
        }

        LineChange other = (LineChange) o;
        return content.equals(other.content) &&
                originalLineNumber == other.originalLineNumber &&
                revisedLineNumber == other.revisedLineNumber &&
                type == other.type;
    }

    @Override
    public int hashCode() {
        return content.hashCode() * 31 +
                originalLineNumber * 31 +
                revisedLineNumber * 31 +
                type.hashCode() * 31;
    }
}

