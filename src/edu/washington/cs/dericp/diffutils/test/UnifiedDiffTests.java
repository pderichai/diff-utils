package edu.washington.cs.dericp.diffutils.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.junit.Test;

import edu.washington.cs.dericp.diffutils.UnifiedDiff;
import edu.washington.cs.dericp.diffutils.Utils;

public class UnifiedDiffTests {
    
    public static final String TEST_DIR = "src/edu/washington/cs/dericp/diffutils/test/";
    public static final String TEST_DIFF_1 = TEST_DIR + "TestDiff1.test";
    public static final String TEST_DIFF_2 = TEST_DIR + "TestDiff2.test";
    public static final String TEST_DIFF_1_OUT = TEST_DIR + "TestDiff1.out";
    public static final String TEST_DIFF_2_OUT = TEST_DIR + "TestDiff2.out";

    @Test
    public void testConstructor() {
        UnifiedDiff unifiedDiff1 = new UnifiedDiff(TEST_DIFF_1);
        unifiedDiff1.exportUnifiedDiff(TEST_DIFF_1_OUT);
        List<String> diffLines1 = Utils.fileToLines(TEST_DIFF_1);
        List<String> diffLines1Out = Utils.fileToLines(TEST_DIFF_1_OUT);
        assertEquals(diffLines1, diffLines1Out);
        
        UnifiedDiff unifiedDiff2 = new UnifiedDiff(TEST_DIFF_2);
        unifiedDiff2.exportUnifiedDiff(TEST_DIFF_2_OUT);
        List<String> diffLines2 = Utils.fileToLines(TEST_DIFF_2);
        List<String> diffLines2Out = Utils.fileToLines(TEST_DIFF_2_OUT);
        assertEquals(diffLines2, diffLines2Out);
    }
}
