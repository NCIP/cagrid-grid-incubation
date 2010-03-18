package org.cagrid.redcap.test.system;

import org.junit.Test;
import gov.nih.nci.cagrid.testing.system.haste.Story;
import org.cagrid.redcap.test.system.RedcapDataServiceTest;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;

public class RCDSTestsStoryBook {
	
	@Test
    public void rcdsTests() throws Throwable {
    	Story s1 = new RedcapDataServiceTest(ServiceContainerFactory
            .createContainer(ServiceContainerType.TOMCAT_CONTAINER));
    	
    	s1.runBare();
   }
}
