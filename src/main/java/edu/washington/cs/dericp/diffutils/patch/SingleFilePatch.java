package edu.washington.cs.dericp.diffutils.patch;

import edu.washington.cs.dericp.diffutils.LineChange;
import edu.washington.cs.dericp.diffutils.diff.SingleFileUnifiedDiff;
import edu.washington.cs.dericp.diffutils.UnifiedHunk;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents changes to a single file.
 */
public class SingleFilePatch {

    private SingleFileUnifiedDiff diff;
    private List<List<LineChange>> hunks;

    public SingleFilePatch(SingleFileUnifiedDiff SFUnifiedDiff) {
        for(UnifiedHunk hunk : SFUnifiedDiff.getHunks()) {
            int originalLineNumber = hunk.getOriginalLineNumber();
            int revisedLineNumber = hunk.getRevisedLineNumber();
            List<LineChange> lineChangesInHunk = new ArrayList<>();

        }
    }

    public List<LineChange> getFileChanges() {
        throw new NotImplementedException();
    }

    public List<List<LineChange>> getContigousFileChanges() {
        throw new NotImplementedException();
    }
}