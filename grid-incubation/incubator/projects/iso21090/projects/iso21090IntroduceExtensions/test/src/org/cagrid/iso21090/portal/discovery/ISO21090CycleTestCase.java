package org.cagrid.iso21090.portal.discovery;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class ISO21090CycleTestCase extends CycleTestCase {
    public ISO21090CycleTestCase(String name) {
        super(name);
    }


    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ISO21090CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
