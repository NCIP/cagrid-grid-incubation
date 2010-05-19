package org.cagrid.redcap.test.system;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.cagrid.redcap.test.system.steps.InvokeClientStep;
import org.cagrid.redcap.test.system.steps.RemoveAuthorizationStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import org.cagrid.redcap.test.system.steps.ConfigureRedcapDataServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeleteServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyCAStep;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.haste.Story;

public class RedcapDataServiceTest extends Story {

	private static final String NODEPLOYVALIDATION = "-Dno.deployment.validation=true";
	private static final String NOINDEXREGISTRATION = "-Dperform.index.service.registration=false";
	
	private RCDSTestCaseInfo rcdsTestCaseInfo;
	
	private ServiceContainer container = null;
	private String tmpDir;
	File serviceDir = null;
	
	private static final Log LOG = LogFactory.getLog(RedcapDataServiceTest.class);
	
	public RedcapDataServiceTest(ServiceContainer container) {
       this.container=container;
	}

    public String getName() {
        return getDescription();
    }

    public String getDescription() {
        return "Redcap Data Service Test";
    }

    protected boolean storySetUp() throws Throwable {
    	
    	boolean copyServiceStep = false;
    	rcdsTestCaseInfo = new RCDSTestCaseInfo();
    	tmpDir = rcdsTestCaseInfo.getTempDir(); 
    	serviceDir = new File(rcdsTestCaseInfo.getTempDir());
    	serviceDir.mkdirs();
    	
    	File srcDir = new File(System.getProperty("basedir"));
    	if(serviceDir.exists() && serviceDir.isDirectory()){
        	CopyServiceStep copyService = new CopyServiceStep(srcDir, serviceDir);
    		try{
    			copyService.runStep();
    		}catch(Throwable e){
    			e.printStackTrace();
    		}
    		copyServiceStep = true;
        }
    	if(copyServiceStep){
    		PropertyConfigurator.configure(tmpDir + File.separator + "test/resources/properties/log4j.properties");
    	}
    	LOG.debug("Copied Service from ["+srcDir+"]to :"+serviceDir);
    	return (container != null && copyServiceStep) ;
    }
    
    protected Vector<Object> steps() {
        Vector<Object> steps = new Vector<Object>();
        try {
        	steps.add(new UnpackContainerStep(container));
            steps.add(new CopyCAStep((SecureContainer) container, serviceDir));
            steps.add(new RemoveAuthorizationStep(tmpDir,rcdsTestCaseInfo));
            steps.add(new ConfigureRedcapDataServiceStep(rcdsTestCaseInfo,container,true));
            List<String> deploymentArgs = Arrays.asList(new String[] {NODEPLOYVALIDATION,NOINDEXREGISTRATION});
            steps.add(new DeployServiceStep(container, tmpDir, deploymentArgs));
            steps.add(new StartContainerStep(container));
            steps.add(new InvokeClientStep(container, rcdsTestCaseInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }

    protected void storyTearDown() throws Throwable {
		DeleteServiceStep step4 = new DeleteServiceStep(new File(rcdsTestCaseInfo.getTempDir()));
        try{
        	step4.runStep();
        }catch(Throwable e){
        	e.printStackTrace();
        }
    	List<Throwable> errors = new LinkedList<Throwable>();
    	StopContainerStep step2 = new StopContainerStep(container);
        try {
            step2.runStep();
        } catch (Throwable e) {
            errors.add(e);
        }
        DestroyContainerStep step3 = new DestroyContainerStep(container);
        try {
            step3.runStep();
        } catch (Throwable e) {
            errors.add(e);
        }
        if (errors.size() != 0) {
            LOG.error("Exception(s) occured during TearDown:");
            for (Throwable t : errors) {
                System.err.println("----------------");
                t.printStackTrace();
            }
            throw new Exception("Exception(s) occured during TearDown");
        }
    }
}
