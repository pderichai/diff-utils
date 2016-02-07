package main.java.edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A UnifiedDiff represents a unified diff file. A unified diff is essentially
 * composed of one or more diffs which are composed of one or more hunks.
 * 
 * An actual unified diff is essentially a collection of diffs. The structure
 * of a unified diff can be though of as:
 * 
 * UnifiedDiff
 *     getDiffs [0]
 *     getDiffs [1]
 *     ...
 *     getDiffs [n]
 *     
 * Note that a Diff has its own internal structure. Please see the
 * related documentation in the {@link main.java.edu.washington.cs.dericp.diffutils.Diff}.
 */
public class UnifiedDiff {
    // TODO representation exposure needs to be removed
    
    // This field changes depending on what signifies a new diff.
    // In some formats, this could be "diff", and in others, "---".
    // TODO if necessary, turn this into a field that can be set in the constructor
    private String diffSplit = "diff";
    private List<Diff> diffs;
    
    /**
     * Constructs a UnifiedDiff object from the unified diff at the
     * specified pathname
     * 
     * @param pathname is the relative or absolute pathname of the diff
     */
    public UnifiedDiff(String pathname) {
        this(Utils.readFile(pathname));
    }
    
    /**
     * Constructs a UnifiedDiff with the specified unified diff lines.
     * 
     * @param unifiedDiffLines is a list of the lines of the unified diff
     */
    public UnifiedDiff(List<String> unifiedDiffLines) {
        parseDiffLines(unifiedDiffLines);
    }
    
    /**
     * Constructs a UnifiedDiff that is a copy of the specified UnifiedDiff.
     * 
     * @param unifiedDiff is the UnifiedDiff to be copied
     */
    public UnifiedDiff(UnifiedDiff unifiedDiff) {
        diffs = new ArrayList<Diff>();
        for (Diff diff : unifiedDiff.diffs) {
            diffs.add(new Diff(diff));
        }
    }
    
    /**
     * Reads in the lines of a unified diff.
     * 
     * @param unifiedDiffLines is the non-null non-empty List of Strings
     *        that represent the unified diff, one String per line of the
     *        diff
     */
    private void parseDiffLines(List<String> unifiedDiffLines) {
        if (unifiedDiffLines == null || unifiedDiffLines.isEmpty()) {
            throw new IllegalArgumentException("Diff is empty");
        }
        
        diffs = new ArrayList<Diff>();
        Iterator<String> iter = unifiedDiffLines.iterator();
        String currentLine = null;
        if (iter.hasNext()) {
            currentLine = iter.next();
        }
        while (iter.hasNext() && currentLine != null) {
            if (currentLine.startsWith(diffSplit)) {
                List<String> diffLines = new ArrayList<String>();
                diffLines.add(currentLine);
                currentLine = iter.next();
                
                // constructing a new Diff
                while (!currentLine.startsWith(diffSplit) && iter.hasNext()) {
                    diffLines.add(currentLine);
                    currentLine = iter.next();
                }
                // if last line of unifiedDiff
                if (!iter.hasNext()) {
                    diffLines.add(currentLine);
                }
                
                // adding the newly constructed Diff to this UnifiedDiff
                diffs.add(new Diff(diffLines));
            } else {
                currentLine = iter.next();
            }
        }
    }
    
    /**
     * Returns the Diffs that compose this UnifiedDiff.
     * 
     * @return a list of Diffs that compose this UnifiedDiff
     */
    public List<Diff> getDiffs() {
        return diffs;
    }
    
    /**
     * Removes a diff from the unified diff. Conceptually, this method undoes
     * all the changes in an entire file from the unified diff.
     * 
     * @param diffNumber is the zero-based index of the diff to be removed
     */
    public void removeDiff(int diffNumber) {
        // Currently implemented as setting the Diff in diffs to null since
        // it is beneficial to know how many Diffs the UnifiedDiff started
        // with.
        if (diffNumber < diffs.size()) {
            diffs.set(diffNumber, null);
        }
    }
    
    /**
     * Removes a hunk from the unified diff. Conceptually, this
     * method undoes all the changes in an entire hunk.
     * 
     * @param diffNumber is the zero-based index of the specified diff
     * @param hunkNumber is the zero-based index of the hunk to be removed
     */
    public void removeHunk(int diffNumber, int hunkNumber) {
        if (diffNumber < diffs.size() && hunkNumber < diffs.get(diffNumber).getHunks().size()) {
            List<Hunk> hunks = diffs.get(diffNumber).getHunks();
            Hunk removedHunk = hunks.get(hunkNumber);
            int offset = removedHunk.getOriginalHunkSize() - removedHunk.getRevisedHunkSize();
            for (int i = hunkNumber + 1; i < diffs.get(diffNumber).getHunks().size(); ++i) {
               hunks.get(i).modifyRevisedLineNumber(offset);
            }
            hunks.set(hunkNumber, null);
        }
    }
    
    /**
     * Removes a change in the unified diff. Conceptually, this undoes a single
     * change in a hunk.
     * 
     * @param diffLine is the exact line in the unified diff that should be
     *        undone
     */
    public void removeChange(String diffLine) {
        for (int diffNum = 0; diffNum < diffs.size(); diffNum++) {
            Diff currentDiff = diffs.get(diffNum);
            for (int hunkNum = 0; hunkNum < currentDiff.getHunks().size(); hunkNum++) {
                Hunk currentHunk = currentDiff.getHunks().get(hunkNum);
                for (int lineNum = 0; lineNum < currentHunk.getModifiedLines().size(); lineNum++) {
                    if (currentHunk.getModifiedLines().get(lineNum).equals(diffLine)) {
                        removeChange(diffNum, hunkNum, lineNum);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Removes a change in the unified diff. Conceptually, this undoes a single
     * change in a hunk.
     * 
     * @param diffNumber is the zero-based index of the diff that the change
     *                   is contained in
     * @param hunkNumber is the zero-based index of the hunk that the change
     *                   is contained in
     * @param lineNumber is the zero-based index of the line number of the
     *                   change in the modified lines of the hunk. The modified
     *                   lines are defined to be all the lines within a hunk
     *                   exclusive of the context lines that exist at the
     *                   beginning and end of a hunk.
     */
    public void removeChange(int diffNumber, int hunkNumber, int lineNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk modifiedHunk = hunks.get(hunkNumber);
        int result = modifiedHunk.removeLine(lineNumber);
        if (result != 0) {
            for (int i = hunkNumber + 1; i < hunks.size(); i++) {
                Hunk currentHunk = hunks.get(i);
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
     * Exports the unified diff as a List of Strings.
     * 
     * @return a list of strings which are the lines of the unified diff
     */
    public List<String> exportUnifiedDiffToLines() {
        List<String> export = new ArrayList<String>();
        for (Diff diff : diffs) {
            if (diff != null) {
                export.addAll(diff.diffToLines());
            }
        }
        return export;
    }
    
    /**
     * Writes the UnifiedDiff to a file.
     * 
     * @param pathname is path where the UnifiedDiff will be written
     */
    public void writeUnifiedDiff(String pathname) {
        Utils.writeFile(exportUnifiedDiffToLines(), pathname);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnifiedDiff)) return false;
        
        UnifiedDiff other = (UnifiedDiff) obj;
        return (diffs.equals(other.diffs));
    }
    
    @Override
    public int hashCode() {
        return diffs.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Diff> diffIter = diffs.iterator();
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
