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
