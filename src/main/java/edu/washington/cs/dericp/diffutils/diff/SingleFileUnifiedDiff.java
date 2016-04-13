package edu.washington.cs.dericp.diffutils.diff;

import edu.washington.cs.dericp.diffutils.change.LineChange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a diff which denotes all changes to a single file.
 * A unified diff is composed of one or more diffs, and a diff is composed
 * of one or more hunks.
 * 
 * An actual diff denotes all the changes to a single file in the
 * unified diff and is composed of one or more hunks. Similarly, a
 * SingleFileUnifiedDiff object is a collection of Hunks which form to represent all
 * the changes to a specified file in a unified diff.
 * 
 * Structure of a diff:
 *     getContextInfo()
 *     getOriginalDiffPath()
 *     getRevisedDiffPath()
 *     getHunks[0]
 *     getHunks[1]
 *     ...
 *     getHunks[n].
 *     
 * Note that a UnifiedHunk has its own internal structure. Please see the related
 * documentation at {@link UnifiedHunk}.
 */
public class SingleFileUnifiedDiff {

    // contextInfo is a List of Strings that contains the lines at the
    // beginning of a single file unified diff that specify the relative paths
    // of the files from which the diff was generated, one String per line
    private List<String> contextInfo;
    // the relative filepath of the original file
    private String originalDiffPath;
    // the relative filepath of the revised file
    private String revisedDiffPath;
    // a List of UnifiedHunk objects which compose this SingleFIleUnifiedDiff
    private List<UnifiedHunk> hunks;
    
    /**
     * Constructs a new SingleFileUnifiedDiff with the specified diffLines.
     * 
     * @param diffLines is a list of the lines of the diff
     */
    public SingleFileUnifiedDiff(List<String> diffLines) {
        contextInfo = new ArrayList<String>();
        hunks = new ArrayList<UnifiedHunk>();
        setContextInfo(diffLines);
        setHunks(diffLines);
    }
    
    /**
     * Constructs a new SingleFileUnifiedDiff that is a copy of the specified SingleFileUnifiedDiff.
     * 
     * @param diff is the SingleFileUnifiedDiff to be copied
     */
    public SingleFileUnifiedDiff(SingleFileUnifiedDiff diff) {
        contextInfo.clear();
        for (String info : diff.contextInfo) {
            contextInfo.add(info);
        }
        originalDiffPath = diff.originalDiffPath;
        revisedDiffPath = diff.revisedDiffPath;
        hunks = new ArrayList<UnifiedHunk>();
        for (UnifiedHunk hunk : diff.hunks) {
            if (hunk == null) {
                hunks.add(null);
            } else {
                hunks.add(new UnifiedHunk(hunk));
            }
        }
    }

    /**
     * Gets the hunks of this SingleFileUnifiedDiff. Modifying the objects in
     * the List of UnifiedHunk objects that is returned will change the state
     * of this SingleFileUnifiedDiff.
     *
     * @return a list of the Hunks of this SingleFileUnifiedDiff
     */
    // TODO: Restructuring this method so that it doesn't expose representation
    // would be a huge win. However, representation exposure is currently
    // necessary for SingleFileUnifiedDiffs to work with MultiFileUnifiedDiffs
    public List<UnifiedHunk> getHunks() {
        return hunks;
    }

    /**
     * Returns the {@link SingleFileUnifiedDiff} at the specified index of this MultiFileUnifiedDiff.
     * Modifying the returned SingleFileUnifiedDiff will modify this MultiFileUnifiedDiff.
     *
     * It is possible for this method to return null if the UnifiedHunk at the
     * specified hunkIndex has already been removed.
     *
     * @param hunkIndex the zero-based index of the hunk in this
     *                  SingleFileUnifiedDiff that will be returned
     * @return the UnifiedHunk at the specified index in this SingleFileUnifiedDiff,
     *         null if it has already been removed from this SingleFileUnifiedDiff
     */
    public UnifiedHunk getHunk(int hunkIndex) {
        return hunks.get(hunkIndex);
    }

    /**
     * Returns the number of UnifiedHunks that are contained in this
     * SingleFileUnifiedPatch. The return value specifies the number
     * of UnifiedHunks that this SingleFileUnifiedPatch was constructed
     * with and is unaffected by the removal of UnifiedHunks from this
     * SingleFileUnifiedDiff
     *
     * @return the number of UnifiedHunks that this SingleFileUnifiedDiff
     *         was constructed with
     */
    public int numHunks() {
        return hunks.size();
    }

    /**
     * Sets the context info of this SingleFileUnifiedDiff.
     * 
     * @param diffLines is a non-null List of Strings that represents a diff, one
     *        String per line of the diff
     */
    private void setContextInfo(List<String> diffLines) {
        contextInfo.clear();
        for (Iterator<String> iter = diffLines.iterator(); iter.hasNext();) {
            String line = iter.next();
            if (line.startsWith("---")) {
                originalDiffPath = line;
                revisedDiffPath = iter.next();
                break;
            }
            contextInfo.add(line);
        }
    }
    
    /**
     * Sets the hunks of this SingleFileUnifiedDiff.
     * 
     * @param diffLines is a non-null non-empty List of Strings that represents
     *        a diff, one String per line of the diff
     */
    private void setHunks(List<String> diffLines) {
        if (diffLines == null || diffLines.isEmpty()) {
            throw new IllegalArgumentException("SingleFileUnifiedDiff is empty");
        }
        
        hunks.clear();
        Iterator<String> iter = diffLines.iterator();
        String line = iter.next();
        
        while (iter.hasNext()) {
            if (line.startsWith("@@")) {
                List<String> hunkLines = new ArrayList<String>();
                hunkLines.add(line);
                
                line = iter.next();
                
                while (!line.startsWith("@@") && iter.hasNext()) {
                    hunkLines.add(line);
                    line = iter.next();
                }
                
                // if last line of SingleFileUnifiedDiff
                if (!iter.hasNext()) {
                    hunkLines.add(line);
                }
                hunks.add(new UnifiedHunk(hunkLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    /**
     * Sets the file paths that specify where the diff should be applied to.
     * 
     * @param originalRelPath is the non-null relative path of the original file
     * @param revisedRelPath is the non-null relative path of the revised file
     */
    public void setFilePaths(String originalRelPath, String revisedRelPath) {
        this.originalDiffPath = "--- a/" + originalRelPath;
        this.revisedDiffPath = "+++ b/" + originalRelPath;
    }
    
    /**
     * Returns the original diff path of this SingleFileUnifiedDiff. The original diff path
     * is the pathname of the file that this SingleFileUnifiedDiff can be applied to.
     * 
     * @return the original diff path of this SingleFileUnifiedDiff
     */
    public String getOriginalDiffPath() {
        return originalDiffPath;
    }
    
    /**
     * Returns the revised diff path of this SingleFileUnifiedDiff. The revised diff path
     * is the pathname of the file at {@link SingleFileUnifiedDiff#getOriginalDiffPath()}
     * after this SingleFileUnifiedDiff is applied.
     * 
     * @return the revised diff path of this SingleFileUnifiedDiff
     */
    public String getRevisedDiffPath() {
        return revisedDiffPath;
    }
    
    /**
     * Returns this SingleFileUnifiedDiff as a List of Strings.
     * 
     * @return a List of Strings, one String per line of this diff
     */
    public List<String> diffToLines() {
        List<String> diff = new ArrayList<String>();
        diff.addAll(contextInfo);
        diff.add(originalDiffPath);
        diff.add(revisedDiffPath);
        for (UnifiedHunk hunk : hunks) {
            if (hunk != null) {
                diff.addAll(hunk.hunkToLines());
            }
        }
        return diff;
    }

    /**
     * Removes a change from this SingleFileUnifiedDiff. Conceptually, all the
     * LineChanges represented by this SingleFileUnifiedDiff with the same
     * content and type will no longer be represented by this
     * SingleFileUnifiedDiff.
     *
     * For example, if this SingleFileUnifiedDiff represented the insertion
     * of three different lines into a file, {one, two, three}, then removing
     * a LineChange with content "one" and of type insertion will result in a
     * SingleFileUnifiedDiff representing the insertion of two lines,
     * {two, three}.
     *
     * @param change the change to be removed
     */
    public void removeChange(LineChange change) {
        for (UnifiedHunk hunk : hunks) {
            hunk.removeChange(change);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof SingleFileUnifiedDiff)) return false;
        
        SingleFileUnifiedDiff other = (SingleFileUnifiedDiff) obj;
        return contextInfo.equals(other.contextInfo) &&
                originalDiffPath.equals(other.originalDiffPath) &&
                revisedDiffPath.equals(other.revisedDiffPath) &&
                hunks.equals(other.hunks);
    }
    
    @Override
    public int hashCode() {
        return contextInfo.hashCode() * originalDiffPath.hashCode()
                * revisedDiffPath.hashCode() * hunks.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String line : contextInfo) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        sb.append(originalDiffPath);
        sb.append(System.lineSeparator());
        sb.append(revisedDiffPath);
        for (UnifiedHunk hunk : hunks) {
            sb.append(System.lineSeparator());
            sb.append(hunk.toString());
        }
        return sb.toString();
    }
}
