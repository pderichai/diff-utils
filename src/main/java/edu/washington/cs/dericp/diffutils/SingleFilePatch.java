package edu.washington.cs.dericp.diffutils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * This class represents changes to a single file.
 */
public class SingleFilePatch {

    private SingleFileUnifiedDiff diff;

    public List<LineChange> getFileChanges() {
        throw new NotImplementedException();
    }

    public List<List<LineChange>> getContigousFileChanges() {
        throw new NotImplementedException();
    }
}
