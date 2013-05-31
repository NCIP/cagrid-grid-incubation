/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
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
