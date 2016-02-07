package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents a hunk. A hunk denotes changes in a contiguous "hunk"
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
 *     getModifiedLines()
 *     getEndContext()
 */
public class Hunk {
    // TODO representation exposure needs to be removed
    
    // TODO make this a parameter for the construction of a hunk
    public static final int CONTEXT_SIZE = 3;
    // the non-abstracted lines of the hunk i.e. what can be directly parsed
    private List<String> originalHunkLines;
    // all the lines exclusive of the CONTEXT_SIZE lines at the beginning
    // and end of the hunk
    private List<String> modifiedLines;
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
     * Constructs a new Hunk with the specified hunk lines.
     * 
     * @param originalHunkLines is a List of Strings that represents the
     *        original lines of the hunk
     */
    public Hunk(List<String> originalHunkLines) { 
        this.originalHunkLines = originalHunkLines;
        setContextInfo(originalHunkLines.get(0));
        modifiedLines = new ArrayList<String>();
        for (int i = CONTEXT_SIZE + 1; i < originalHunkLines.size() - CONTEXT_SIZE; ++i) {
            modifiedLines.add(originalHunkLines.get(i));
        }
    }
    
    /**
     * Constructs a Hunk that is a copy of the specified Hunk.
     * 
     * @param hunk is the Hunk to be copied
     */
    public Hunk(Hunk hunk) {
        originalLineNumber = hunk.originalLineNumber;
        originalHunkSize = hunk.originalHunkSize;
        revisedLineNumber = hunk.revisedLineNumber;
        revisedHunkSize = hunk.revisedHunkSize;
        originalHunkLines = new ArrayList<String>();
        originalHunkLines.addAll(hunk.originalHunkLines);
        modifiedLines = new ArrayList<String>();
        modifiedLines.addAll(hunk.modifiedLines);
        filenameInfo = hunk.filenameInfo;
    }
    
    /**
     * Sets the context information of this Hunk.
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
     * Gets the context information of this Hunk.
     * 
     * @return the context information of this Hunk
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
     * Gets the modified lines of this Hunk. Modified lines are defined to be
     * the lines of the hunk between the context lines found at the top and
     * bottom of each hunk.
     * 
     * @return a List of Strings that represent the modified lines of this hunk,
     *         one String per line
     */
    public List<String> getModifiedLines() {
        return modifiedLines;
    }
    
    /**
     * Gets the start context of this Hunk. The start context is defined to be
     * the lines immediately following the context information in a hunk that
     * precede the modified lines.
     * 
     * @return a List of Strings represents the context lines of this hunk, one
     *         String per line of context
     */
    public List<String> getStartContext() {
        List<String> startContext = new ArrayList<String>();
        for (int i = 1; i <= CONTEXT_SIZE; ++i) {
            if (originalHunkLines.get(i).charAt(0) != '+' || originalHunkLines.get(i).charAt(0) != '-') {
                startContext.add(originalHunkLines.get(i));
            }
        }
        return startContext;
    }
    
    /**
     * Gets the end context of this Hunk. The end context is defined to be the
     * lines immediately following the modified lines in a hunk.
     * 
     * @return  the context lines at the end of a hunk
     */
    public List<String> getEndContext() {
        List<String> endContext = new ArrayList<String>();
        for (int i = originalHunkLines.size() - CONTEXT_SIZE; i < originalHunkLines.size(); ++i) {
            if (originalHunkLines.get(i).charAt(0) != '+' || originalHunkLines.get(i).charAt(0) != '-') {
                endContext.add(originalHunkLines.get(i));
            }
        }
        return endContext;
    }
    
    
    /**
     * Returns this Hunk as a List of Strings.
     * 
     * @return a List of Strings that represent the lines of this Hunk
     */
    public List<String> hunkToLines() {
        List<String> hunkLines = new ArrayList<String>();
        hunkLines.add(getContextInfo());
        hunkLines.addAll(getStartContext());
        hunkLines.addAll(getModifiedLines());
        hunkLines.addAll(getEndContext());
        return hunkLines;
    }
    
    /**
     * Removes a line from this Hunk.
     * 
     * @param lineNumber is an int that specifies the zero-based index of the
     *        of the line in the modified lines section to be removed from
     *        this Hunk
     * @return an int that denotes the kind of line removed:
     *             1 if the removed line was an insertion
     *             -1 if the removed line was a deletion
     *             0 if the lineNumber did not specify a valid change
     */
    public int removeLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= modifiedLines.size()) {
            throw new IllegalArgumentException("Line number is out of bounds");
        }
        String line = getModifiedLines().get(lineNumber);
        if (line.startsWith("+")) {
            modifiedLines.set(lineNumber, null);
            --revisedHunkSize;
            return 1;
        }
        if (line.startsWith("-")) {
            // essentially turns the minus line into a context line
            modifiedLines.set(lineNumber, line.replaceFirst("\\-", " "));
            ++revisedHunkSize;
            return -1;
        }
        return 0;
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
    
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hunk)) return false;
        
        Hunk other = (Hunk) obj;
        return originalHunkLines.equals(other.originalHunkLines) &&
                modifiedLines.equals(other.modifiedLines) &&
                originalLineNumber == other.originalLineNumber &&
                originalHunkSize == other.originalHunkSize &&
                revisedLineNumber == other.revisedLineNumber &&
                revisedHunkSize == other.revisedHunkSize &&
                filenameInfo.equals(other.filenameInfo);
    }
    
    @Override
    public int hashCode() {
        return originalHunkLines.hashCode() *
                modifiedLines.hashCode() *
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

