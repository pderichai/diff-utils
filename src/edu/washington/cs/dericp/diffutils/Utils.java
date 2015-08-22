package edu.washington.cs.dericp.diffutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    
    /**
     * Converts a File to a List of Strings.
     * 
     * @param filePath      a String, the path of the file on the machine
     * @return              a List of Strings, containing the information of the file
     *                      at the designated file-path
     */
    public static List<String> fileToLines(String filePath) {
        List<String> lines = new ArrayList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
            in.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return lines;
    }
    
    /**
     * Converts a List of Strings to a File.
     * 
     * @param fileLines     a List of Strings, contains the information to be output in the file
     * @param filePath      a String, the path of the file on the machine
     * @return
     */
    public static File linesToFile(List<String> fileLines, String filePath) {
        File file = new File(filePath);
        try {
            PrintStream output = new PrintStream(file);
            for (String line : fileLines) {
                if (line != null) {
                    output.println(line);
                }
            }
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }
}
