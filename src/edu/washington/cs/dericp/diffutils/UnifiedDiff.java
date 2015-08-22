package edu.washington.cs.dericp.diffutils;

public class UnifiedDiff {
    
    /* multiple diff handling not yet supported */
    
    private Diff diff;
    
    public UnifiedDiff(Diff diff) {
        this.diff = diff;
    }
    
    public Diff getDiff() {
        return diff;
    }
    
    // zero based indexing
    public void removeHunk(int hunkNumber) {
        Hunk hunk = diff.getHunks().get(hunkNumber);
        int offset = hunk.getOriginalHunkSize() - hunk.getRevisedHunkSize();
        for (int i = hunkNumber + 1; i < diff.getHunks().size(); ++i) {
           diff.getHunks().get(i).modifyRevisedLineNumber(offset);
        }
        diff.getHunks().set(hunkNumber, null);
    }
    
    // zero based indexing
    public void removeChangeFromHunk(int hunkNumber, int lineNumber) {
        int result = diff.getHunks().get(hunkNumber).removeLine(lineNumber);
        if (result != 0) {
            for (int i = hunkNumber + 1; i < diff.getHunks().size(); i++) {
                if (result == 1) {
                    diff.getHunks().get(i).modifyRevisedLineNumber(-1);
                }
                if (result == -1) {
                    diff.getHunks().get(i).modifyRevisedLineNumber(1);
                }
            }
        }
    }
    
    public void exportUnifiedDiff(String filepath) {
        diff.exportDiff(filepath);
    }
}
