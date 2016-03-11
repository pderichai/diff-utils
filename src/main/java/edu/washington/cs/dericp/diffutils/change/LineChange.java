package edu.washington.cs.dericp.diffutils.change;

/**
 * <p>This class represents a single change in a patch.</p>
 */
public class LineChange {

    /**
     * There are three types of LineChanges. Insertions, deletions,
     * context lines which are not changes at all but are lines present
     * in both the original and revised versions of the file.
     */
    public enum Type {
        INSERTION, DELETION, CONTEXT
    }

    private String content;
    private int originalLineNumber;
    private int revisedLineNumber;
    private Type type;

    /**
     * Constructs a new LineChange.
     *
     * @param content the content of the change i.e. what is to be deleted
     *                or inserted
     * @param originalLineNumber where this change occurs in the original file
     * @param revisedLineNumber where this change is seen in the revised file
     * @param type the type of change to be created
     */
    public LineChange(String content, int originalLineNumber, int revisedLineNumber, Type type) {
        this.content = content;
        this.originalLineNumber = originalLineNumber;
        this.revisedLineNumber = revisedLineNumber;
        this.type = type;
    }

    /**
     * Sets the type of this change.
     *
     * @param type the type of change that this LineChange will now represent
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Returns the content of this LineChange.
     *
     * @return the content of this LineChange
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the original line number of this LineChange.
     *
     * @return where this change occurs in the original file
     */
    public int getOriginalLineNumber() {
        return originalLineNumber;
    }

    /**
     * Returns the revised line number of this LineChange
     *
     * @return where this change is seen in the revised file
     */
    public int getRevisedLineNumber() {
        return revisedLineNumber;
    }

    /**
     * Returns the type of this LineChange.
     *
     * @return the type of this LineChange.
     */
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

