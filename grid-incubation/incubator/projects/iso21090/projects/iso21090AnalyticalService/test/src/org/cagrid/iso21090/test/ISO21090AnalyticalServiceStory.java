package org.cagrid.iso21090.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.iso21090.test.steps.TestIsoMethodsStep;

import gov.nih.nci.cagrid.testing.core.TestingConstants;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerPorts;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerProperties;
import gov.nih.nci.cagrid.testing.system.deployment.GlobusServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.NoAvailablePortException;
import gov.nih.nci.cagrid.testing.system.deployment.PortFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatSecureServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.TomcatServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

public class ISO21090AnalyticalServiceStory extends Story {

	ServiceContainer container = null;
	EndpointReferenceType containerEpr = null;
	
	@Override
	public String getDescription() {
		return "ISO21090AnalyticalService System Test Story";
	}
	
	protected boolean storySetUp() {
		try {
			container = createContainer();
			containerEpr = container.getServiceEPR("cagrid/ISO21090AnalyticalService");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	protected ServiceContainer createContainer() throws IOException {
		// The freaking ServiceContainerFactory hardcodes 
		// the temp directory to "tmp"
		
		ServiceContainerType type = ServiceContainerType.TOMCAT_CONTAINER;
		ContainerPorts ports = PortFactory.getContainerPorts();
		
		File containerTempDir = new File("testContainer");
        if (!containerTempDir.exists()) {
        	containerTempDir.mkdirs();
        }
        
        File tempContainerDir = File.createTempFile(
        		type.toString(), "tmp", containerTempDir);
        
        // create a directory, not a file
        tempContainerDir.delete();
        tempContainerDir.mkdirs();
        
        String zipLocation = type.getZip();
	    File containerZip = new File(zipLocation);
	    ContainerProperties props = new ContainerProperties(tempContainerDir,
	        	containerZip, ports, false,
	        	null, null, null);
	        
	    return new TomcatServiceContainer(props);
	}

	@Override
	protected Vector steps() {
		Vector<Step> steps = new Vector<Step>();
		
        /////////////////////////////////////////////////////
        // Unpack container
        /////////////////////////////////////////////////////
        steps.add(new UnpackContainerStep(container));
        
        /////////////////////////////////////////////////////
        // Deploy service to container
        /////////////////////////////////////////////////////;
        steps.add(new DeployServiceStep(container, 
        	new File("./").getAbsolutePath(), 
        	Arrays.asList(new String[]{"-Dno.deployment.validation=true"})));

        /////////////////////////////////////////////////////
        // Start up container
        /////////////////////////////////////////////////////
        steps.add(new StartContainerStep(container));
       
        /////////////////////////////////////////////////////
        // Can we test now?
        /////////////////////////////////////////////////////
        steps.add(new TestIsoMethodsStep(containerEpr));


        return steps;
    }


    @Override
    protected void storyTearDown() throws Throwable {
        new StopContainerStep(container).runStep();
        new DestroyContainerStep(container).runStep();
    }

}
