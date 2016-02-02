package test.edu.washington.cs.dericp.diffutils;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import main.edu.washington.cs.dericp.diffutils.UnifiedDiff;
import main.edu.washington.cs.dericp.diffutils.Utils;

public class UnifiedDiffTests {
    
    public static final String TEST_DIR = "src/test/edu/washington/cs/dericp/diffutils/";
    public static final String TEST_DIFF_1 = TEST_DIR + "TestDiff1.test";
    public static final String TEST_DIFF_2 = TEST_DIR + "TestDiff2.test";
    public static final String TEST_DIFF_3 = TEST_DIR + "TestDiff3.test";
    public static final String TEST_DIFF_1_OUT = TEST_DIR + "TestDiff1.out";
    public static final String TEST_DIFF_2_OUT = TEST_DIR + "TestDiff2.out";
    public static final String TEST_DIFF_3_OUT = TEST_DIR + "TestDiff3.out";
    
    private UnifiedDiff unifiedDiff1;
    private UnifiedDiff unifiedDiff2;
    private UnifiedDiff unifiedDiff3;
    
    @Before
    public void setup() {
        unifiedDiff1 = new UnifiedDiff(TEST_DIFF_1);
        unifiedDiff2 = new UnifiedDiff(TEST_DIFF_2);
        unifiedDiff3 = new UnifiedDiff(TEST_DIFF_3);
    }
    
    @Test
    public void testConstructor() {
        unifiedDiff1.writeUnifiedDiff(TEST_DIFF_1_OUT);
        List<String> diffLines1 = Utils.readFile(TEST_DIFF_1);
        List<String> diffLines1Out = Utils.readFile(TEST_DIFF_1_OUT);
        assertEquals(diffLines1, diffLines1Out);
        
        unifiedDiff2.writeUnifiedDiff(TEST_DIFF_2_OUT);
        List<String> diffLines2 = Utils.readFile(TEST_DIFF_2);
        List<String> diffLines2Out = Utils.readFile(TEST_DIFF_2_OUT);
        assertEquals(diffLines2, diffLines2Out);
        
        unifiedDiff3.writeUnifiedDiff(TEST_DIFF_3_OUT);
        List<String> diffLines3 = Utils.readFile(TEST_DIFF_3);
        List<String> diffLines3Out = Utils.readFile(TEST_DIFF_3_OUT);
        assertEquals(diffLines3, diffLines3Out);
    }
    
    @Test
    public void testRemoveDiff() {
        unifiedDiff3.removeDiff(0);
        assertEquals(unifiedDiff3.exportUnifiedDiffToLines(), Utils.readFile(TEST_DIR + "TestDiff3RemoveDiff(0).expected"));
    }
    
    @Test
    public void testRemoveHunk() {
        unifiedDiff1.removeHunk(0, 2);
        assertEquals(unifiedDiff1.exportUnifiedDiffToLines(), Utils.readFile(TEST_DIR + "TestDiff1RemoveHunk(0, 2).expected"));
    }
    
    @Test
    public void testRemoveChange() {
        unifiedDiff1.removeChange(0, 2, 0);
        assertEquals(unifiedDiff1.exportUnifiedDiffToLines(), Utils.readFile(TEST_DIR + "TestDiff1RemoveChange(0, 2, 0).expected"));
    }
}
