package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a diff which denotes all changes to a single file.
 * A unified diff is composed of one or more diffs, and a diff is composed
 * of one or more hunks.
 */
class Diff {
    
    // Structure of a diff:
    //     contextInfo
    //     originalDiffPath
    //     revisedDiffPath
    //     hunk 1
    //     hunk 2
    //     ...
    //     hunk n
    
    // all the information above the two relative relative paths in a diff
    private List<String> contextInfo;
    
    private String originalDiffPath;
    private String revisedDiffPath;
    private List<Hunk> hunks;
    
    /**
     * Constructs a new Diff with the specified diffLines.
     * 
     * @param diffLines is a list of the lines of the diff
     */
    public Diff(List<String> diffLines) {
        setContextInfo(diffLines);
        setHunks(diffLines);
    }
    
    /**
     * Constructs a new Diff that is a copy of the specified Diff.
     * 
     * @param diff is the Diff to be copied
     */
    public Diff(Diff diff) {
        contextInfo = new ArrayList<String>();
        for (String info : diff.contextInfo) {
            contextInfo.add(info);
        }
        originalDiffPath = diff.originalDiffPath;
        revisedDiffPath = diff.revisedDiffPath;
        hunks = new ArrayList<Hunk>();
        for (Hunk hunk : diff.hunks) {
            if (hunk == null) {
                hunks.add(null);
            } else {
                hunks.add(new Hunk(hunk));
            }
        }
    }
    
    /**
     * Gets the hunks of this Diff.
     * 
     * @return a list of the Hunks of this Diff
     */
    public List<Hunk> getHunks() {
        return hunks;
    }
    
    /**
     * Sets the context info of this Diff.
     * 
     * @param diffLines is a list of the lines of the diff
     * @requires diffLines != null
     * @modifies this
     * @effects the context information (essentially all the information
     *          before the first hunk) will be set to the diff context
     *          information found in diffLines
     */
    private void setContextInfo(List<String> diffLines) {
        contextInfo = new ArrayList<String>();
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
     * Sets the hunks of this Diff.
     * 
     * @param diffLines is a list of the lines of the diff
     * @requires diffLines != null
     * @modifies this
     * @effects the hunks of this Diff will be set
     */
    private void setHunks(List<String> diffLines) {
        hunks = new ArrayList<Hunk>();
        if (diffLines.isEmpty()) {
            throw new IllegalArgumentException("Diff is empty");
        }
        
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
                
                // if last line of Diff
                if (!iter.hasNext()) {
                    hunkLines.add(line);
                }
                hunks.add(new Hunk(hunkLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    /**
     * Sets the file paths that the diff will be applied to.
     * 
     * @param relPathA is the relative path of the original file
     * @param relPathB is the relative path of the revised file
     * @requires relPathA != null
     *           relPathB != null
     * @modifies this
     * @effects the two pathnames that this diff affects, namely
     *          the pathname of the original file and the pathname
     *          of the revised file, will be set in this Diff
     */
    public void setFilePaths(String originalRelPath, String revisedRelPath) {
        this.originalDiffPath = "--- a/" + originalRelPath;
        this.revisedDiffPath = "+++ b/" + originalRelPath;
    }
    
    /**
     * Returns this Diff as a List of Strings.
     * 
     * @return a List of Strings that represents the lines of this Diff
     */
    public List<String> diffToLines() {
        List<String> diff = new ArrayList<String>();
        diff.addAll(contextInfo);
        diff.add(originalDiffPath);
        diff.add(revisedDiffPath);
        for (Hunk hunk : hunks) {
            if (hunk != null) {
                diff.addAll(hunk.hunkToLines());
            }
        }
        return diff;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Diff)) return false;
        
        Diff other = (Diff) obj;
        return contextInfo.equals(other.contextInfo)
                && originalDiffPath.equals(other.originalDiffPath)
                && revisedDiffPath.equals(other.revisedDiffPath)
                && hunks.equals(other.hunks);
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
        for (Hunk hunk : hunks) {
            sb.append(System.lineSeparator());
            sb.append(hunk.toString());
        }
        return sb.toString();
    }
}
