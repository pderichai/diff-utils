package edu.washington.cs.dericp.diffutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This class provides utility functions that are used throughout diff-utils.
 */
public class Utils {
    
    public static final boolean DEBUG = true;
    
    /**
     * Converts the file specified at pathname to a list of strings.
     * 
     * @param pathname      a String, the path of the file on the machine
     * @return              a List of Strings representing the lines of the file
     *                      at the specified pathname
     */
    public static List<String> fileToLines(String pathname) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathname));
            while (reader.ready()) {
                lines.add(reader.readLine());
            }
            reader.close();
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converts a List of Strings to a File.
     * 
     * @param fileLines are the lines of file stored as a List of Strings
     * @param pathname is the path of the file to be created/modified
     * @return
     */
    public static void linesToFile(List<String> fileLines, String pathname) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathname));
            Iterator<String> lineIter = fileLines.iterator();
            if (lineIter.hasNext()) {
                writer.write(lineIter.next());
            }
            while (lineIter.hasNext()) {
                writer.newLine();
                writer.write(lineIter.next());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
