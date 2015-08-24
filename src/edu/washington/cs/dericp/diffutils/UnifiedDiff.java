package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.List;

public class UnifiedDiff {
    
    /* multiple diff handling not yet supported */
    
    private List<Diff> diffs;
    
    public UnifiedDiff(List<Diff> diffs) {
        this.diffs = diffs;
    }
    
    public List<Diff> getDiffs() {
        return diffs;
    }
    
    // zero based indexing
    public void removeHunk(int diffNumber, int hunkNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk removedHunk = hunks.get(hunkNumber);
        int offset = removedHunk.getOriginalHunkSize() - removedHunk.getRevisedHunkSize();
        for (int i = hunkNumber + 1; i < diffs.get(diffNumber).getHunks().size(); ++i) {
           hunks.get(i).modifyRevisedLineNumber(offset);
        }
        hunks.set(hunkNumber, null);
    }
    
    // zero based indexing
    public void removeChangeFromHunk(int diffNumber, int hunkNumber, int lineNumber) {
        List<Hunk> hunks = diffs.get(diffNumber).getHunks();
        Hunk modifiedHunk = hunks.get(hunkNumber);
        int result = modifiedHunk.removeLine(lineNumber);
        if (result != 0) {
            for (int i = hunkNumber + 1; i < hunks.size(); i++) {
                if (result == 1) {
                    hunks.get(i).modifyRevisedLineNumber(-1);
                }
                if (result == -1) {
                    hunks.get(i).modifyRevisedLineNumber(1);
                }
            }
        }
    }
    
    public void exportUnifiedDiff(String filePath) {
        List<String> export = new ArrayList<String>();
        for (Diff diff : diffs) {
            export.addAll(diff.diffToLines());
        }
        Utils.linesToFile(export, filePath);
    }
}
