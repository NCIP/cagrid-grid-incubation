/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
	            .createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER));
		s1.runBare();
   }
}
