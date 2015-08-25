package edu.washington.cs.dericp.diffutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hunk {
    
    public static final int CONTEXT_SIZE = 3;
    
    private List<String> originalHunkLines;
    private List<String> modifiedLines;
    private int originalLineNumber;
    private int originalHunkSize;
    private int revisedLineNumber;
    private int revisedHunkSize;
    private String fileNameInfo;
    
    public Hunk(List<String> originalHunkLines) { 
        this.originalHunkLines = originalHunkLines;
        setContextInfo(originalHunkLines.get(0));
        modifiedLines = new ArrayList<String>();
        for (int i = CONTEXT_SIZE + 1; i < originalHunkLines.size() - CONTEXT_SIZE; ++i) {
            modifiedLines.add(originalHunkLines.get(i));
        }
    }
    
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
    
    public String getContextInfo() {
        return ("@@ -" + originalLineNumber + ',' + originalHunkSize + " +" + revisedLineNumber + ',' + revisedHunkSize + " @@" + " " + fileNameInfo);
    }
    
    public List<String> getModifiedLines() {
        return modifiedLines;
    }
    
    public List<String> getStartContext() {
        List<String> startContext = new ArrayList<String>();
        for (int i = 1; i <= CONTEXT_SIZE; ++i) {
            if (originalHunkLines.get(i).charAt(0) != '+' || originalHunkLines.get(i).charAt(0) != '-') {
                startContext.add(originalHunkLines.get(i));
            }
        }
        return startContext;
    }
    
    public List<String> getEndContext() {
        List<String> endContext = new ArrayList<String>();
        for (int i = originalHunkLines.size() - CONTEXT_SIZE; i < originalHunkLines.size(); ++i) {
            if (originalHunkLines.get(i).charAt(0) != '+' || originalHunkLines.get(i).charAt(0) != '-') {
                endContext.add(originalHunkLines.get(i));
            }
        }
        return endContext;
    }
    
    public List<String> hunkToLines() {
        List<String> hunkLines = new ArrayList<String>();
        hunkLines.add(getContextInfo());
        hunkLines.addAll(getStartContext());
        hunkLines.addAll(getModifiedLines());
        hunkLines.addAll(getEndContext());
        return hunkLines;
    }
    
    // removes a modified line, zero based index
    // returns 1 if the line was a +, -1 if it was a -,
    // and 0 if it was a context line
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
    
    public int getOriginalHunkSize() {
        return originalHunkSize;
    }
    
    public int getRevisedHunkSize() {
        return revisedHunkSize;
    }
    
    public void modifyRevisedLineNumber(int change) {
        revisedLineNumber = revisedLineNumber + change;
    }
}

