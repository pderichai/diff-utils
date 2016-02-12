package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>A Patch represents a patch file and is a collection of {@link SingleFileUnifiedDiff}s.</p>
 *
 * <p>A single-file unified diff is a unified diff that represents changes to a
 * single file.</p>
 *
 * <p>A Patch provides methods for modifying the patch that it represents.
 * If the Patch was created from a patch file, these changes will not modify
 * the actual file. However, Patch provides a method to write the patch that
 * this Patch instance represents to a file.</p>
 *
 * <p>A Patch provides methods to access information about the patch it represents.
 * In particular, a Patch allows the user to obtain a SingleFileUnifiedDiff
 * instance that represents any of the unified diffs of a single file in this
 * Patch.</p>
 *
 * <p>A Patch provides a method to return the patch that it represents as a List
 * of Strings.</p>
 *
 * <p>A Patch provides a method to write the patch that it represents to a file.</p>
 */
public class Patch {
    
    // This field changes depending on what signifies a new diff.
    // In some formats, this could be "diff", and in others, "---".
    // TODO if necessary, turn this into a field that can be set in the constructor
    private String diffSplit = "diff";
    private List<SingleFileUnifiedDiff> diffs;
    
    /**
     * Constructs a Patch from the patch at the specified pathname.
     * 
     * @param pathname the relative or absolute pathname of the patch that this
     *                 Patch instance will represent
     */
    public Patch(String pathname) {
        this(Utils.readFile(pathname));
    }
    
    /**
     * Constructs a Patch consisting of the given lines.
     * 
     * @param patchLines a List of Strings that represents the patch that
     *                   this Patch instance will represent, one String per
     *                   line of the patch
     */
    public Patch(List<String> patchLines) {
        parsePatchLines(patchLines);
    }
    
    /**
     * Constructs a Patch that is a copy of the specified Patch.
     * 
     * @param patch the Patch to be copied
     */
    public Patch(Patch patch) {
        diffs = new ArrayList<SingleFileUnifiedDiff>();
        for (SingleFileUnifiedDiff diff : patch.diffs) {
            diffs.add(new SingleFileUnifiedDiff(diff));
        }
    }
    
    /**
     * Reads in the lines of a patch.
     * 
     * @param patchLines the non-null non-empty List of Strings
     *        that represent a patch, one string per line of the
     *        patch
     */
    private void parsePatchLines(List<String> patchLines) {
        if (patchLines == null || patchLines.isEmpty()) {
            throw new IllegalArgumentException("SingleFileUnifiedDiff is empty");
        }
        
        diffs = new ArrayList<SingleFileUnifiedDiff>();
        Iterator<String> iter = patchLines.iterator();
        String currentLine = null;
        if (iter.hasNext()) {
            currentLine = iter.next();
        }
        while (iter.hasNext() && currentLine != null) {
            if (currentLine.startsWith(diffSplit)) {
                List<String> diffLines = new ArrayList<String>();
                diffLines.add(currentLine);
                currentLine = iter.next();
                
                // constructing a new SingleFileUnifiedDiff
                while (!currentLine.startsWith(diffSplit) && iter.hasNext()) {
                    diffLines.add(currentLine);
                    currentLine = iter.next();
                }
                // if last line of unifiedDiff
                if (!iter.hasNext()) {
                    diffLines.add(currentLine);
                }
                
                // adding the newly constructed SingleFileUnifiedDiff to this Patch
                diffs.add(new SingleFileUnifiedDiff(diffLines));
            } else {
                currentLine = iter.next();
            }
        }
    }
    
    /**
     * Returns the {@link SingleFileUnifiedDiff}s that compose this Patch. Modifying a
     * SingleFileUnifiedDiff in the List that is returned will modify this Patch.
     * Modifying the List itself will not affect this Patch.
     * 
     * @return a list of {@link SingleFileUnifiedDiff}s that compose this Patch
     */
    public List<SingleFileUnifiedDiff> getDiffs() {
        return new ArrayList<SingleFileUnifiedDiff>(diffs);
    }

    /**
     * Returns the {@link SingleFileUnifiedDiff} at the specified index of this Patch.
     * Modifying the returned SingleFileUnifiedDiff will modify this Patch.
     * @param diffIndex the zero-based index of the single-file unified diff in
     *                  this Patch that will be returned
     * @return a SingleFileUnifiedDiff instance that represents the specified
     *         single-file unified diff in this Patch
     */
    public SingleFileUnifiedDiff getDiff(int diffIndex) {
        return diffs.get(diffIndex);
    }

    /**
     * Returns the number of SingleFileUnifiedDiffs contained in this patch.
     * The return value specifies the number of SingleFileUnifiedDiffs that
     * this Patch was constructed with and is unaffected by the removal of
     * SingleFileUnifiedDiffs from this Patch.
     *
     * @return the number of SingleFileUnifiedDiffs that this Patch was
     *         constructed with
     */
    public int numDiffs() {
       return diffs.size();
    }

    /**
     * Removes a unified diff from this Patch. Conceptually, all the changes
     * denoted by the specified single-file unified diff in this Patch will
     * no longer be represented by this Patch. Once a unified diff is removed,
     * the indices of the single-file unified diffs in this Patch will remain
     * unchanged.
     * 
     * @param diffIndex the zero-based index of the single-file unified diff
     *                  to be removed
     */
    public void removeDiff(int diffIndex) {
        // Currently implemented as setting the SingleFileUnifiedDiff in diffs to null since
        // it is beneficial to know how many UnifiedDiffs the Patch started
        // with. If this behavior changes, the specification also needs to change.
        if (diffIndex < diffs.size()) {
            diffs.set(diffIndex, null);
        }
    }
    
    /**
     * Removes a unified hunk from a single-file unified diff in this Patch.
     * Conceptually, all the changes denoted by the specified unified hunk
     * in this Patch will no longer be represented by this Patch. Once a
     * unified hunk is removed, the indices of the unified hunks in the
     * specified unified diff will remain unchanged.
     * 
     * @param diffIndex the zero-based index of the single-file unified diff that
     *                   contains the unified hunk to be removed
     * @param hunkIndex the zero-based index of the unified hunk to be removed
     *                   from within the specified unified diff
     */
    public void removeHunk(int diffIndex, int hunkIndex) {
        if (diffIndex < diffs.size() && hunkIndex < diffs.get(diffIndex).getHunks().size()) {
            List<UnifiedHunk> hunks = diffs.get(diffIndex).getHunks();
            UnifiedHunk removedHunk = hunks.get(hunkIndex);
            int offset = removedHunk.getOriginalHunkSize() - removedHunk.getRevisedHunkSize();
            for (int i = hunkIndex + 1; i < diffs.get(diffIndex).getHunks().size(); ++i) {
               hunks.get(i).modifyRevisedLineNumber(offset);
            }
            hunks.set(hunkIndex, null);
        }
    }

    // TODO this method needs to be changed completely
    /**
     * THIS METHOD IS BUGGY AND THE DOCUMENTATION IS INCORRECT! please ignore.
     *
     * Removes a change in the unified diff. Conceptually, this undoes a single
     * change in a hunk.
     * 
     * @param diffLine is the exact line in the unified diff that should be
     *        undone
     */
    public void removeLine(String diffLine) {
        for (int diffNum = 0; diffNum < diffs.size(); diffNum++) {
            SingleFileUnifiedDiff currentDiff = diffs.get(diffNum);
            for (int hunkNum = 0; hunkNum < currentDiff.getHunks().size(); hunkNum++) {
                UnifiedHunk currentHunk = currentDiff.getHunks().get(hunkNum);
                for (int lineNum = 0; lineNum < currentHunk.getModifiedLines().size(); lineNum++) {
                    if (currentHunk.getModifiedLines().get(lineNum).equals(diffLine)) {
                        removeLine(diffNum, hunkNum, lineNum);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Removes a line from the specified unified hunk in the specified single-file
     * unified diff in this Patch. Conceptually, the change denoted by the specified
     * line will no longer be represented by this Patch. Once a line is removed,
     * the indices of the lines in the specified unified hunk will remain
     * unchanged.
     *
     * @param diffIndex the zero-based index of the single-file unified diff that
     *                  contains the line to be removed
     * @param hunkIndex the zero-based index of the hunk that contains the
     *                  line to be removed
     * @param lineIndex is the zero-based index of the line to be removed in
     *                  the {@link UnifiedHunk#getModifiedLines()}
     *                  of the specified unified hunk and unified diff
     */
    public void removeLine(int diffIndex, int hunkIndex, int lineIndex) {
        List<UnifiedHunk> hunks = diffs.get(diffIndex).getHunks();
        UnifiedHunk modifiedHunk = hunks.get(hunkIndex);
        int result = modifiedHunk.removeLine(lineIndex);
        if (result != 0) {
            for (int i = hunkIndex + 1; i < hunks.size(); i++) {
                UnifiedHunk currentHunk = hunks.get(i);
                if (currentHunk != null) {
                    if (result == 1) {
                        hunks.get(i).modifyRevisedLineNumber(-1);
                    }
                    if (result == -1) {
                        hunks.get(i).modifyRevisedLineNumber(1);
                    }
                }
            }
        }
    }
    
    /**
     * Gets the lines of this patch.
     * 
     * @return a List of Strings that represents the lines of the patch that
     *         this Patch instance represents, one string per line of the patch
     */
    public List<String> getPatchLines() {
        List<String> export = new ArrayList<String>();
        for (SingleFileUnifiedDiff diff : diffs) {
            if (diff != null) {
                export.addAll(diff.diffToLines());
            }
        }
        return export;
    }
    
    /**
     * Writes the patch that this Patch instance represents to a file.
     * 
     * @param pathname path where the patch will be written
     */
    public void writeUnifiedDiff(String pathname) {
        Utils.writeFile(getPatchLines(), pathname);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Patch)) return false;
        
        Patch other = (Patch) obj;
        return (diffs.equals(other.diffs));
    }
    
    @Override
    public int hashCode() {
        return diffs.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<SingleFileUnifiedDiff> diffIter = diffs.iterator();
        if (diffIter.hasNext()) {
            sb.append(diffs.get(0).toString());
        }
        while (diffIter.hasNext()) {
            sb.append(System.lineSeparator());
            sb.append(diffIter.next());
        }
        return sb.toString();
    }
}
