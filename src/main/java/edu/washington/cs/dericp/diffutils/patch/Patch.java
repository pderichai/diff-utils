package edu.washington.cs.dericp.diffutils.patch;

import edu.washington.cs.dericp.diffutils.change.LineChange;

import java.util.List;

public interface Patch {
    List<LineChange> getChanges();
}
