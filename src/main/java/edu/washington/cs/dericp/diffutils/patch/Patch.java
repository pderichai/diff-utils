package edu.washington.cs.dericp.diffutils.patch;

import edu.washington.cs.dericp.diffutils.LineChange;
import edu.washington.cs.dericp.diffutils.diff.MultiFileUnifiedDiff;
import edu.washington.cs.dericp.diffutils.diff.SingleFileUnifiedDiff;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * This class is a container for multiple SingleFilePatches.
 */
public class Patch {
    private MultiFileUnifiedDiff MFUnifiedDiff;
    private List<SingleFilePatch> singleFilePatches;

    public Patch(MultiFileUnifiedDiff MFUnifiedDiff) {
        for (SingleFileUnifiedDiff SFUnifiedDiff : MFUnifiedDiff.getDiffs()) {
            singleFilePatches.add(new SingleFilePatch(SFUnifiedDiff));
        }
    }

    public List<LineChange> getChanges() {
        throw new NotImplementedException();
    }

    public List<List<LineChange>> getContiguousChanges() {
        throw new NotImplementedException();
    }
}
