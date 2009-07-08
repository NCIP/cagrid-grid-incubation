package org.cagrid.identifiers.namingauthority.test;

/*
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class NATestCase extends TestCase {
    private static Log log = LogFactory.getLog(NATestCase.class);

   public static void main(String[] args) {
      junit.textui.TestRunner.run(NATestCase.class);
   }
}

