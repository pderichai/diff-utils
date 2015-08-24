package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Diff {
    
    private String filePathA;
    private String filePathB;
    private List<Hunk> hunks;
    
    public Diff(List<String> diffLines) {
        hunks = new ArrayList<Hunk>();
        setFilePaths(diffLines);
        readHunks(diffLines);
    }
    
    
    public List<Hunk> getHunks() {
        return hunks;
    }
    
    public Diff(String diffFilePath) {
        this(Utils.fileToLines(diffFilePath));
    }
    
    private void setFilePaths(List<String> diffLines) {
        for (Iterator<String> iter = diffLines.iterator(); iter.hasNext();) {
            String line = iter.next();
            if (line.startsWith("---")) {
                filePathA = line;
                filePathB = iter.next();
                break;
            }
        }
    }
    
    private void readHunks(List<String> diffLines) {
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
                
                // if last line of Hunk
                if (!iter.hasNext()) {
                    hunkLines.add(line);
                }
                hunks.add(new Hunk(hunkLines));
            } else {
                line = iter.next();
            }
        }
    }
    
    public List<String> diffToLines() {
        List<String> diff = new ArrayList<String>();
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
