package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A hunk denotes changes in a specific continuous section of code.
 */
public class Hunk {
    
    public static final int CONTEXT_SIZE = 3;
    
    private List<String> originalHunkLines;
    private List<String> modifiedLines;
    private int originalLineNumber;
    private int originalHunkSize;
    private int revisedLineNumber;
    private int revisedHunkSize;
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
        return ("@@ -" + originalLineNumber + ',' + originalHunkSize + " +" + revisedLineNumber + ',' + revisedHunkSize + " @@" + " " + fileNameInfo);
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
}

