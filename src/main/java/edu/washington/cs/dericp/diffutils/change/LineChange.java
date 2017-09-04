package edu.washington.cs.dericp.diffutils.change;

/**
 * <p>This class represents a change to a single line in a patch. In a patch
 * file, lines may be either deleted inserted, or remain the same. In the case
 * that a line remains the same between two different revisions of a file, the
 * line can be known as a context line.</p>
 */
public class LineChange {

    /**
     * There are three types of LineChanges: insertion, deletion,
     * context. Context LineChanges are lines which are not changes
     * at all but are lines present
     * in both the original and revised versions of the file.
     */
    public enum Type {
        INSERTION, DELETION, CONTEXT
    }

    // the content of the line that this LineChange represents
    private String content;
    // the line number of the content of this LineChange in the original file
    // if a line was inserted between the original and revised versions of a
    // file, there is no need for an originalLineNumber since the line did not
    // previously exist in the original file
    private int originalLineNumber;
    // the line number of the content of this LineChange in the revised file
    // if a line was deleted between the original and revised versions of a
    // file, there is no need for a revisedLineNumber since the line no longer
    // exists in the new file
    private int revisedLineNumber;
    // the type of this LineChange
    private Type type;

    /**
     * Constructs a new LineChange.
     *
     * @param content the content of the change i.e. what is to be deleted
     *                or inserted and should be a full line
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
        int result = 17;
        result = 31 * result + content.hashCode();
        result = 31 * result + originalLineNumber;
        result = 31 * result + revisedLineNumber;
        result = 31 * result + type.hashCode();
        return result;
    }
}

