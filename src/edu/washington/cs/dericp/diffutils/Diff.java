package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Diff {
    
    private List<String> contextInfo;
    private String filePathA;
    private String filePathB;
    private List<Hunk> hunks;
    
    public Diff(List<String> diffLines) {
        setContextInfo(diffLines);
        readHunks(diffLines);
    }
    
    public List<Hunk> getHunks() {
        return hunks;
    }
    
    public Diff(String filePath) {
        this(Utils.fileToLines(filePath));
    }
    
    // Special constructor that copies a diff.
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
    
    public void setFilePaths(String filePathA, String filePathB) {
        this.filePathA = "--- a/" + filePathA;
        this.filePathB = "+++ b/" + filePathB;
    }
    
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
