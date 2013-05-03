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
package edu.umn.msi.cagrid.introduce.interfaces;

import edu.umn.msi.cagrid.introduce.interfaces.TestInterface;
import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;


public class TestSource {
    @ImplementsForService(interfaces = {"edu.umn.msi.cagrid.introduce.interfaces.TestInterface"})
    TestInterface testInterfaceImpl;

    @ImplementsForService(interfaces = {"edu.umn.msi.cagrid.introduce.interfaces.TestInterface2", "edu.umn.msi.cagrid.introduce.interfaces.TestInterface3"})
    Object impl2; 

    String anotherObject;
}
