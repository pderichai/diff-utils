package edu.washington.cs.dericp.diffutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Utility functions that are used throughout the diff utility.
 */
class Utils {
    
    /**
     * Converts a File to a List of Strings.
     * 
     * @param filePath      a String, the path of the file on the machine
     * @return              a List of Strings, containing the information of the file
     *                      at the designated file-path
     * @throws IOException 
     */
    public static List<String> fileToLines(String relFilePath) throws IOException {
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader(relFilePath));
        while (reader.ready()) {
            lines.add(reader.readLine());
        }
        reader.close();
        return lines;
    }
    
    /**
     * Converts a List of Strings to a File.
     * 
     * @param fileLines     a List of Strings, contains the information to be output in the file
     * @param filePath      a String, the path of the file on the machine
     * @return
     * @throws IOException 
     */
    public static void linesToFile(List<String> fileLines, String relFilePath) throws IOException {
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
    }
}
