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