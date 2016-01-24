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
 * Utility functions that are used throughout diff-utils.
 */
public class Utils {
    
    public static final boolean DEBUG = true;
    
    /**
     * Converts the file specified at relFilePath to a list of strings.
     * 
     * @param filePath      a String, the path of the file on the machine
     * @return              a List of Strings, containing the information of the file
     *                      at the designated file-path
     */
    public static List<String> fileToLines(String relFilePath) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(relFilePath));
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
     * @param fileLines     a List of Strings, contains the information to be output in the file
     * @param filePath      a String, the path of the file on the machine
     * @return
     */
    public static void linesToFile(List<String> fileLines, String relFilePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(relFilePath));
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
