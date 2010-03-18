package org.cagrid.redcap.test.system;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.redcap.test.system.steps.InvokeClientStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import org.cagrid.redcap.test.system.steps.ConfigureRedcapDataServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeleteServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;

public class RedcapDataServiceTest extends ServiceStoryBase {

	private RCDSTestCaseInfo tci = new RCDSTestCaseInfo();
	private static final String NODEPLOYVALIDATION = "-Dno.deployment.validation=true";
	private static final String NOINDEXREGISTRATION = "-Dperform.index.service.registration=false";
	
	public RedcapDataServiceTest(ServiceContainer container) {
       super(container);
       PropertyConfigurator.configure("." + File.separator + "test"+File.separator + "resources" + File.separator + "properties" + File.separator + "log4j.properties");
    }
    
    public RedcapDataServiceTest() {
        try {
            this.setContainer(ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
    }

    public String getName() {
        return getDescription();
    }

    public String getDescription() {
        if(getContainer().getProperties().isSecure()){
            return "Secure Transfer Service Test";
        }
        return "Transfer Service Test";
    }


    protected Vector<Object> steps() {
        Vector<Object> steps = new Vector<Object>();
        try {
        	steps.add(new UnpackContainerStep(getContainer()));
            System.out.println("skipping index service registration...................................");
        	List<String> deploymentArgs = Arrays.asList(new String[] {NODEPLOYVALIDATION,NOINDEXREGISTRATION});
            steps.add(new ConfigureRedcapDataServiceStep(tci,getContainer(),false));
            steps.add(new DeployServiceStep(getContainer(), "tmp/RedcapDataService", deploymentArgs));
            steps.add(new StartContainerStep(getContainer()));
            steps.add(new InvokeClientStep(getContainer(), tci));
            steps.add(new StopContainerStep(getContainer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    protected boolean storySetUp() throws Throwable {
    	DeleteServiceStep step4 = new DeleteServiceStep(new File("tmp"));
         try{
         	step4.runStep();
         }catch(Throwable e){
         	e.printStackTrace();
         }
        File tempService = new File("tmp/RedcapDataService");
		//File asLocation = new File("C:\\deletewrkspace\\RedcapDataService");
        File asLocation = new File(".");
		CopyServiceStep copyService = new CopyServiceStep(asLocation,tempService);
		try{
			copyService.runStep();
		}catch(Throwable e){
			e.printStackTrace();
		}
        return true;
    }

    protected void storyTearDown() throws Throwable {
    	DeleteServiceStep step4 = new DeleteServiceStep(new File("tmp/RedcapDataService"));
        try{
        	step4.runStep();
        }catch(Throwable e){
        	e.printStackTrace();
        }
    	StopContainerStep step2 = new StopContainerStep(getContainer());
        try {
            step2.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        DestroyContainerStep step3 = new DestroyContainerStep(getContainer());
        try {
            step3.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
