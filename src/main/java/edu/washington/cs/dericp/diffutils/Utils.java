package edu.washington.cs.dericp.diffutils;

import edu.washington.cs.dericp.diffutils.change.LineChange;

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
 * Utils is a collection of static methods and does not represent anything.
 */
public class Utils {

    public static final boolean DEBUG = true;

    /**
     * This private constructor prevents clients from instantiating Utils.
     */
    private Utils() {

    }

    /**
     * Reads the file at the specified pathname and returns it as a List of
     * Strings, one per line of the file.
     *
     * @param pathname a String, the path of the file on the machine
     * @return a List of Strings representing the lines of the file
     * at the specified pathname
     */
    public static List<String> readFile(String pathname) {
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
     * Writes a List of Strings, each String representing a line in the file,
     * to the file at the specified pathname.
     *
     * @param fileLines are the lines of file stored as a List of Strings, one
     *                  String per line of the file
     * @param pathname  is the path of the file to be created/modified
     */
    public static void writeFile(List<String> fileLines, String pathname) {
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

    public static LineChange.Type getType(String line) {
        if (line.startsWith("+")) {
            return LineChange.Type.INSERTION;
        } else if (line.startsWith("-")) {
            return LineChange.Type.DELETION;
        } else {
            return LineChange.Type.CONTEXT;
        }
    }

    public static String transformIntoDiffLine(LineChange change) {
        if (change.getType() == LineChange.Type.INSERTION) {
            return "+" + change.getContent();
        } else if (change.getType() == LineChange.Type.DELETION) {
            return "-" + change.getContent();
        } else {
            return " " + change.getContent();
        }
    }
}
