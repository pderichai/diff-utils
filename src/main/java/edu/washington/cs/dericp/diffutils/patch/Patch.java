package edu.washington.cs.dericp.diffutils.patch;

import edu.washington.cs.dericp.diffutils.change.LineChange;

import java.util.List;

/**
 * <p>A Patch is a collection of changes to one or more files.</p>
 *
 * <p>A Patch provides a method to obtain all the changes that this Patch
 * represents.</p>
 *
 * <p>A Patch provides a method for the removal of a change with {@link #removeChange(LineChange)}.
 * This is currently the only way of correctly modifying a Patch.</p>
 *
 * <p>A Patch can be written a to a file or returned as a List of Strings,
 * one String per line of the Patch.</p>
 */
// TODO: Implement addChange TODO: clarifiy documentation in getChanges
public interface Patch {

    /**
     * Returns a List of LineChanges that are the changes this Patch
     * represents.
     *
     * @return the changes that this patch represents
     */
    List<LineChange> getChanges();

    /**
     * Returns a List of Strings that represent the lines of this Patch,
     * one String per line.
     *
     * @return the lines of this Patch
     */
    List<String> getPatchLines();

    /**
     * Removes a change from this Patch.
     *
     * @param change the change to be removed
     */
    void removeChange(LineChange change);

    /**
     * Writes this Patch to a file.
     *
     * @param pathname the relative or absolute pathname of the file where
     *                 this Patch will be written
     */
    void writePatch(String pathname);
}
