package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A hunk denotes changes in a specific continuous section of code.
 */
class Hunk {
    
    public static final int CONTEXT_SIZE = 3;
    
    // the non-abstracted lines of the hunk i.e. what can be directly parsed
    private List<String> originalHunkLines;
    // all the lines exclusive of the (CONTEXT_SIZE) lines at the beginning
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
    private String fileNameInfo;
    
    /**
     * Constructs a new Hunk.
     * 
     * @param originalHunkLines     the lines of the hunk
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
     * Constructs a copy of a hunk in a new instance.
     * 
     * @param other     the hunk that is to be copied
     */
    public Hunk(Hunk other) {
        originalLineNumber = other.originalLineNumber;
        originalHunkSize = other.originalHunkSize;
        revisedLineNumber = other.revisedLineNumber;
        revisedHunkSize = other.revisedHunkSize;
        originalHunkLines = new ArrayList<String>();
        originalHunkLines.addAll(other.originalHunkLines);
        modifiedLines = new ArrayList<String>();
        modifiedLines.addAll(other.modifiedLines);
        fileNameInfo = other.fileNameInfo;
    }
    
    /**
     * Sets the context information of this hunk.
     * 
     * @param contextInfo   the line of hunk context information
     */
    public void setContextInfo(String contextInfo) {
        Scanner input = new Scanner(contextInfo);
        input.useDelimiter("[^0-9]+");
        originalLineNumber = input.nextInt();
        originalHunkSize = input.nextInt();
        revisedLineNumber = input.nextInt();
        revisedHunkSize = input.nextInt();
        input.close();
        fileNameInfo = contextInfo.replaceFirst("@@.*@@ ", "");
    }
    
    /**
     * Gets the context information for this hunk.
     * 
     * @return  the context information of this hunk
     */
    public String getContextInfo() {
        return ("@@ -" + originalLineNumber + ',' + originalHunkSize
                + " +" + revisedLineNumber + ',' + revisedHunkSize + " @@"
                // TODO sometimes the fileNameInfo is empty. This case needs to
                // be handled.
                + " " + fileNameInfo);
    }
    
    /**
     * Gets the lines between the default context lines of the hunk.
     * 
     * @return  the lines of a hunk where changes can occur
     */
    public List<String> getModifiedLines() {
        return modifiedLines;
    }
    
    /**
     * Gets the lines at the beginning of a hunk that give context.
     * 
     * @return  the context lines at the beginning of a hunk
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
     * Gets the lines at the end of a hunk that give context.
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
     * Returns this hunk as a List of Strings.
     * 
     * @return  the lines of this hunk
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
     * Removes a modified line given zero based indexing.
     * Returns 1 if the line was an insertion, -1 if it was a deletion,
     * and 0 if it was a context line
     * 
     * @return  an int that denotes the kind of line removed
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
     * Returns the size of the original hunk.
     * 
     * @return  the size of the original hunk
     */
    public int getOriginalHunkSize() {
        return originalHunkSize;
    }
    
    /**
     * Returns the size of the revised hunk.
     * 
     * @return  the size of the revised hunk
     */
    public int getRevisedHunkSize() {
        return revisedHunkSize;
    }
    
    /**
     * Changes the revised line number by a given amount.
     * 
     * @param change    the amount that the revised line number will be changed
     */
    public void modifyRevisedLineNumber(int change) {
        revisedLineNumber = revisedLineNumber + change;
    }
    
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Hunk)) return false;
        
        Hunk other = (Hunk) obj;
        return originalHunkLines.equals(other.originalHunkLines)
                && modifiedLines.equals(other.modifiedLines)
                && originalLineNumber == other.originalLineNumber
                && originalHunkSize == other.originalHunkSize
                && revisedLineNumber == other.revisedLineNumber
                && revisedHunkSize == other.revisedHunkSize
                && fileNameInfo.equals(other.fileNameInfo);
    }
    
    @Override
    public int hashCode() {
        return originalHunkLines.hashCode()
                * modifiedLines.hashCode()
                * originalLineNumber
                * originalHunkSize
                * revisedHunkSize
                * revisedLineNumber
                * revisedHunkSize
                * fileNameInfo.hashCode();
    }
    
    @Override
    public String toString() {
        // TODO implement this toString
        return "";
    }
}

