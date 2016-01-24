package edu.washington.cs.dericp.diffutils.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.Test;

public class UnifiedDiffTests {

    @Test
    public void testConstructor() {
        System.out.println(System.getProperty("user.dir"));
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/edu/washington/cs/dericp/diffutils/test/TestDiff.test"));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
