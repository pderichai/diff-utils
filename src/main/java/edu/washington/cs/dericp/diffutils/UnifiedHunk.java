package edu.washington.cs.dericp.diffutils;

import edu.washington.cs.dericp.diffutils.change.LineChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents a hunk. A hunk denotes changes in a contiguous "hunk"
 * TODO fix this documentation
 * of code. As long as changes are within a certain context size of each other,
 * they can be considered in the same hunk. Each diff is composed of one or
 * more hunks.
 * 
 * Hunks are the components of a diff and can be grouped together in
 * addition to the context information of a diff to denote all the changes
 * to a single file in a unified diff.
 * 
 * Structure of a hunk:
 *     getContextInfo()
 *     getStartContext()
 *     getHunkLines()
 *     getEndContext()
 */
public class UnifiedHunk {
    // TODO representation exposure needs to be removed
    
    // TODO make this a parameter for the construction of a hunk
    public static final int CONTEXT_SIZE = 3;
    // all the lines exclusive of the CONTEXT_SIZE lines at the beginning
    // and end of the hunk
    private List<LineChange> hunkLines;
    // where the hunk starts in the original file
    private int originalLineNumber;
    // the size of the hunk in the original file
    private int originalHunkSize;
    // where the hunk starts in the revised file
    private int revisedLineNumber;
    // the size of the hunk in the revised file
    private int revisedHunkSize;
    // the context information of a hunk that sits in-line next to the
    // hunk size and line number information
    private String filenameInfo;
    
    /**
     * Constructs a new UnifiedHunk with the specified hunk lines.
     * 
     * @param originalHunkLines is a List of Strings that represents the
     *        original lines of the hunk
     */
    public UnifiedHunk(List<String> originalHunkLines) {
        setContextInfo(originalHunkLines.get(0));
        hunkLines = new ArrayList<LineChange>();
        int currentOriginalLineNum = originalLineNumber;
        int currentRevisedLineNum = revisedLineNumber;
        // starting at i = 1 to skipe the line number and hunk size information
        for (int i = 1; i < originalHunkLines.size(); ++i) {
            String line = originalHunkLines.get(i);
            LineChange.Type lineType = Utils.getType(line);
            if (lineType == LineChange.Type.INSERTION) {
                hunkLines.add(new LineChange(line.substring(1), -1, currentRevisedLineNum, Utils.getType(line)));
                currentRevisedLineNum++;
            } else if (lineType == LineChange.Type.DELETION) {
                hunkLines.add(new LineChange(line.substring(1), currentOriginalLineNum, -1, Utils.getType(line)));
                currentOriginalLineNum++;
            } else {
                hunkLines.add(new LineChange(line.substring(1), currentOriginalLineNum, currentRevisedLineNum, Utils.getType(line)));
                currentOriginalLineNum++;
                currentRevisedLineNum++;
            }
        }
    }
    
    /**
     * Constructs a UnifiedHunk that is a copy of the specified UnifiedHunk.
     * 
     * @param hunk is the UnifiedHunk to be copied
     */
    public UnifiedHunk(UnifiedHunk hunk) {
        originalLineNumber = hunk.originalLineNumber;
        originalHunkSize = hunk.originalHunkSize;
        revisedLineNumber = hunk.revisedLineNumber;
        revisedHunkSize = hunk.revisedHunkSize;
        hunkLines = new ArrayList<LineChange>();
        hunkLines.addAll(hunk.hunkLines);
        filenameInfo = hunk.filenameInfo;
    }
    
    /**
     * Sets the context information of this UnifiedHunk.
     * 
     * @param contextInfo is the line of hunk context information will precede
     *        the context lines of this hunk
     */
    public void setContextInfo(String contextInfo) {
        Scanner input = new Scanner(contextInfo);
        input.useDelimiter("[^0-9]+");
        originalLineNumber = input.nextInt();
        originalHunkSize = input.nextInt();
        revisedLineNumber = input.nextInt();
        revisedHunkSize = input.nextInt();
        input.close();
        filenameInfo = contextInfo.replaceFirst("@@.*@@", "").trim();
    }
    
    /**
     * Gets the context information of this UnifiedHunk.
     * 
     * @return the context information of this UnifiedHunk
     */
    public String getContextInfo() {
        String contextInfo = "@@ -" + originalLineNumber + ',' + originalHunkSize +
                " +" + revisedLineNumber + ',' + revisedHunkSize + " @@";
        if (!filenameInfo.isEmpty()) {
            contextInfo += " " + filenameInfo;
        }
        return contextInfo;
    }
    
    /**
     * Gets the modified lines of this UnifiedHunk. Modified lines are defined to be
     * the lines of the hunk between the context lines found at the top and
     * bottom of each hunk.
     * 
     * @return a List of Strings that represent the modified lines of this hunk,
     *         one String per line
     */
    public List<LineChange> getHunkLines() {
        return hunkLines;
    }

    /**
     * Returns this UnifiedHunk as a List of Strings.
     * 
     * @return a List of Strings that represent the lines of this UnifiedHunk
     */
    public List<String> hunkToLines() {
        List<String> hunkLines = new ArrayList<String>();
        hunkLines.add(getContextInfo());
        for (LineChange change : getHunkLines()) {
            hunkLines.add(Utils.transformIntoDiffLine(change));
        }
        return hunkLines;
    }
    
    /**
     * Removes a line from this UnifiedHunk.
     * 
     * @param lineNumber is an int that specifies the zero-based index of the
     *        of the line in the modified lines section to be removed from
     *        this UnifiedHunk
     * @return an int that denotes the kind of line removed:
     *             1 if the removed line was an insertion
     *             -1 if the removed line was a deletion
     *             0 if the lineNumber did not specify a valid change
     */
    public int removeLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= hunkLines.size()) {
            throw new IllegalArgumentException("Line number is out of bounds");
        }
        LineChange change = getHunkLines().get(lineNumber);
        if (change.getType() == LineChange.Type.INSERTION) {
            hunkLines.set(lineNumber, null);
            --revisedHunkSize;
            return 1;
        }
        if (change.getType() == LineChange.Type.DELETION) {
            // essentially turns the minus line into a context line
            change.setType(LineChange.Type.CONTEXT);
            ++revisedHunkSize;
            return -1;
        }
        return 0;
    }

    public int getOriginalLineNumber() {
        return originalLineNumber;
    }

    public int getRevisedLineNumber() {
        return revisedLineNumber;
    }
    
    /**
     * Returns the size of the original hunk, that is, the size of this hunk
     * in the original file.
     * 
     * @return an int that specifies the size of the original hunk
     */
    public int getOriginalHunkSize() {
        return originalHunkSize;
    }
    
    /**
     * Returns the size of the revised hunk, that is, the size of the hunk
     * in the revised file.
     * 
     * @return  the size of the revised hunk
     */
    public int getRevisedHunkSize() {
        return revisedHunkSize;
    }
    
    /**
     * Changes the revised line number by a given amount. The revised line
     * number is where this hunk starts in the revised line.
     * 
     * @param change is the amount that the revised line number will be changed
     */
    public void modifyRevisedLineNumber(int change) {
        revisedLineNumber += change;
    }

    public void removeChange(LineChange change) {
        int changeIndex = hunkLines.indexOf(change);
        if (changeIndex != -1) {
            removeLine(changeIndex);
        }
    }
    
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof UnifiedHunk)) return false;
        
        UnifiedHunk other = (UnifiedHunk) obj;
        return //originalHunkLines.equals(other.originalHunkLines) &&
                hunkLines.equals(other.hunkLines) &&
                originalLineNumber == other.originalLineNumber &&
                originalHunkSize == other.originalHunkSize &&
                revisedLineNumber == other.revisedLineNumber &&
                revisedHunkSize == other.revisedHunkSize &&
                filenameInfo.equals(other.filenameInfo);
    }
    
    @Override
    public int hashCode() {
        return //originalHunkLines.hashCode() *
                hunkLines.hashCode() *
                originalLineNumber *
                originalHunkSize *
                revisedHunkSize *
                revisedLineNumber *
                revisedHunkSize *
                filenameInfo.hashCode();
    }
    
    @Override
    public String toString() {
        // TODO implement this toString
        return "";
    }
}

