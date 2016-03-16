package edu.washington.cs.dericp.diffutils;

import static org.junit.Assert.*;

import edu.washington.cs.dericp.diffutils.change.LineChange;
import edu.washington.cs.dericp.diffutils.diff.MultiFileUnifiedDiff;
import edu.washington.cs.dericp.diffutils.patch.Patch;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by dpang on 3/11/16.
 */
public class PatchTests {
    public static final String TEST_DIR = "src/test/java/edu/washington/cs/dericp/diffutils/";
    public static final String TEST_DIFF_1 = TEST_DIR + "TestDiff1.test";
    public static final String TEST_DIFF_2 = TEST_DIR + "TestDiff2.test";
    public static final String TEST_DIFF_3 = TEST_DIR + "TestDiff3.test";
    public static final String TEST_DIFF_1_OUT = TEST_DIR + "TestDiff1.out";
    public static final String TEST_DIFF_2_OUT = TEST_DIR + "TestDiff2.out";
    public static final String TEST_DIFF_3_OUT = TEST_DIR + "TestDiff3.out";

    private Patch patch1;
    private Patch patch2;
    private Patch patch3;

    @Before
    public void setup() {
        patch1 = new MultiFileUnifiedDiff(TEST_DIFF_1);
        patch2 = new MultiFileUnifiedDiff(TEST_DIFF_2);
        patch3 = new MultiFileUnifiedDiff(TEST_DIFF_3);
    }

    @Test
    public void testConstructor() {
        try {
            patch1.writePatch(TEST_DIFF_1_OUT);
            List<String> diffLines1 = Utils.readFile(TEST_DIFF_1);
            List<String> diffLines1Out = Utils.readFile(TEST_DIFF_1_OUT);
            assertEquals(diffLines1, diffLines1Out);

            patch2.writePatch(TEST_DIFF_2_OUT);
            List<String> diffLines2 = Utils.readFile(TEST_DIFF_2);
            List<String> diffLines2Out = Utils.readFile(TEST_DIFF_2_OUT);
            assertEquals(diffLines2, diffLines2Out);

            patch3.writePatch(TEST_DIFF_3_OUT);
            List<String> diffLines3 = Utils.readFile(TEST_DIFF_3);
            List<String> diffLines3Out = Utils.readFile(TEST_DIFF_3_OUT);
            assertEquals(diffLines3, diffLines3Out);
        } catch (IOException e) {
            fail("A test diff could not be read.");
            e.printStackTrace();
        }
    }

    @Test
    public void testRemoveLineChange() {
        try {
            patch1.removeChange(new LineChange("     *", 170, -1, LineChange.Type.DELETION));
            assertEquals(patch1.getPatchLines(), Utils.readFile(TEST_DIR + "TestDiff1RemoveChange(0, 2, 0).expected"));
        } catch (IOException e) {
            fail("A test diff could not be read");
            e.printStackTrace();
        }
    }
}

