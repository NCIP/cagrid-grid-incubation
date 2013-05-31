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
package org.cagrid.redcap.test.system;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import java.io.File;

public class RCDSTestCaseInfo extends TestCaseInfo {
    
    public String getDir() {
        return getName();
    }

    public String getTempDir() {
    	return "../../../tmp/RedcapDataService";
    }
    
    public String getName() {
        return "RedcapDataService";
    }

    public String getNamespace() {
        return "http://org.cagrid.redcap.system.test/SystemTest";
    }

    public String getPackageDir() {
        return getPackageName().replace('.',File.separatorChar);
    }

    public String getPackageName() {
        return "org.cagrid.redcap.system.test";
    }

    public String getResourceFrameworkType() {
        return IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE;
    }
   
}
