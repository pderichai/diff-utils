package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A diff denotes changes to a specific file.
 */
public class Diff {
    
    private List<String> contextInfo;
    private String filePathA;
    private String filePathB;
    private List<Hunk> hunks;
    
    /**
     * Constructs a new diff.
     * 
     * @param diffLines     the lines of the diff
     */
    public Diff(List<String> diffLines) {
        setContextInfo(diffLines);
        readHunks(diffLines);
    }
    
    
    /**
     * Constructs a diff from a file at a given file path
     * 
     * @param filePath  the file path of the diff file
     */
    public Diff(String filePath) {
        this(Utils.fileToLines(filePath));
    }
    
    /**
     * Constructs a copy of a diff in a new instance.
     * 
     * @param other     the diff that is to be copied
     */
    public Diff(Diff other) {
        contextInfo = new ArrayList<String>();
        for (String info : other.contextInfo) {
            contextInfo.add(info);
        }
        filePathA = other.filePathA;
        filePathB = other.filePathB;
        hunks = new ArrayList<Hunk>();
        for (Hunk hunk : other.hunks) {
            if (hunk == null) {
                hunks.add(null);
            } else {
                hunks.add(new Hunk(hunk));
            }
        }
    }
    
    /**
     * Gets the hunks of this diff.
     * 
     * @return  the hunks of this diff
     */
    public List<Hunk> getHunks() {
        return hunks;
    }
    
    /**
     * Sets the context info of the diff
     * 
     * @param diffLines     the lines of the diff
     */
    private void setContextInfo(List<String> diffLines) {
        contextInfo = new ArrayList<String>();
        for (Iterator<String> iter = diffLines.iterator(); iter.hasNext();) {
            String line = iter.next();
            if (line.startsWith("---")) {
                filePathA = line;
                filePathB = iter.next();
                break;
            }
            contextInfo.add(line);
        }
    }
    
    /**
     * Reads in the hunks of the diff
     * 
     * @param diffLines     the lines of the diff
     */
    private void readHunks(List<String> diffLines) {
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
     * @param filePathA     the first file path
     * @param filePathB     the second file path
     */
    public void setFilePaths(String filePathA, String filePathB) {
        this.filePathA = "--- a/" + filePathA;
        this.filePathB = "+++ b/" + filePathB;
    }
    
    /**
     * Returns this diff as a List of Strings.
     * 
     * @return      the lines of the diff
     */
    public List<String> diffToLines() {
        List<String> diff = new ArrayList<String>();
        diff.addAll(contextInfo);
        diff.add(filePathA);
        diff.add(filePathB);
        for (Hunk hunk : hunks) {
            if (hunk != null) {
                diff.addAll(hunk.hunkToLines());
            }
        }
        return diff;
    }
}
