package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A unified diff denotes all changes to a project across multiple files.
 */
public class UnifiedDiff {
    
    // This field changes depending on what signifies a new diff.
    // In some formats, this could be "diff", and in others, "---".
    public static final String DIFF_SPLIT = "diff";
    private List<Diff> diffs;
    
    /**
     * Constructs a new unified diff.
     * 
     * @param unifiedDiffLines  the lines of the unified diff
     */
    public UnifiedDiff(List<String> unifiedDiffLines) {
        readDiffs(unifiedDiffLines);
    }
    
    /**
     * Constructs a new unified diff.
     * 
     * @param filePath  the file path of the unified diff
     */
    public UnifiedDiff(String filePath) {
        this(Utils.fileToLines(filePath)); 
    }
    
    /**
     * Constructs a copy of a unified diff in a new instance.
     * 
     * @param other     the unified diff to be copied
     */
    public UnifiedDiff(UnifiedDiff other) {
        diffs = new ArrayList<Diff>();
        for (Diff diff : other.diffs) {
            diffs.add(new Diff(diff));
        }
    }
    
    /**
     * Reads in the diffs of a unified diff.
     * 
     * @param unifiedDiffLines  the lines of the unified diff
     */
    private void readDiffs(List<String> unifiedDiffLines) {
        diffs = new ArrayList<Diff>();
        if (unifiedDiffLines.isEmpty()) {
            throw new IllegalArgumentException("Diff is empty");
        }
        
        Iterator<String> iter = unifiedDiffLines.iterator();
        String line = iter.next();
        
        while (iter.hasNext()) {
            if (line.startsWith(DIFF_SPLIT)) {
                List<String> diffLines = new ArrayList<String>();
                diffLines.add(line);
                
                line = iter.next();
                
                while (!line.startsWith(DIFF_SPLIT) && iter.hasNext()) {
                    diffLines.add(line);
                    line = iter.next();
                }
                
                // if last line of unifiedDiff
                if (!iter.hasNext()) {
                    diffLines.add(line);
                }
                diffs.add(new Diff(diffLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    /**
     * Gets the diffs of the unified diff.
     * 
     * @return  the diffs of the unified diff
     */
    public List<Diff> getDiffs() {
        return diffs;
    }
    
    /**
     * Removes a diff from the unified diff. Sets the index of the diff
     * in the list of diffs to null.
     * 
     * @param diffNumber    the index of the diff to be removed
     */
    public void removeDiff(int diffNumber) {
        diffs.set(diffNumber, null);
    }
    
    /**
     * Removes a hunk from a diff. Sets the index of the hunk to null.
     * 
     * @param diffNumber    the index of the relevant diff
     * @param hunkNumber    the index of the hunk to be removed
     */
    public void removeHunk(int diffNumber, int hunkNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk removedHunk = hunks.get(hunkNumber);
        int offset = removedHunk.getOriginalHunkSize() - removedHunk.getRevisedHunkSize();
        for (int i = hunkNumber + 1; i < diffs.get(diffNumber).getHunks().size(); ++i) {
           hunks.get(i).modifyRevisedLineNumber(offset);
        }
        hunks.set(hunkNumber, null);
    }
    
    /**
     * Removes a change from a hunk.
     * 
     * @param diffNumber    the index of the relevant diff
     * @param hunkNumber    the index of the relevant hunk
     * @param lineNumber    the index of the line in the list of
     *                      modified lines to be removed
     */
    public void removeChangeFromHunk(int diffNumber, int hunkNumber, int lineNumber) {
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
     * Exports the unified diff to a file.
     * 
     * @param filePath  the file path where the unified diff will be exported
     */
    public void exportUnifiedDiff(String filePath) {
        List<String> export = new ArrayList<String>();
        for (Diff diff : diffs) {
            export.addAll(diff.diffToLines());
        }
        Utils.linesToFile(export, filePath);
    }
}
