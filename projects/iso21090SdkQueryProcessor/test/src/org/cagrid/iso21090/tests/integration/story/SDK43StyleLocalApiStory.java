package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.iso21090.tests.integration.SDK43ServiceStyleSystemTestConstants;
import org.cagrid.iso21090.tests.integration.steps.CreateDataServiceStep;
import org.cagrid.iso21090.tests.integration.steps.InstallStyleStep;
import org.cagrid.iso21090.tests.integration.steps.InvokeDataServiceStep;

public class SDK43StyleLocalApiStory extends Story {
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer container = null;

    public SDK43StyleLocalApiStory() {
        super();
    }


    public String getDescription() {
        return "Creates an SDK 4.3 with ISO types style data service, configures it to use the local API, deploys and invokes it.";
    }
    
    
    public String getName() {
        return "SDK 4_3 with ISO types Style data service using local API creation and invocation test";
    }
    
    
    public boolean storySetUp() throws Throwable {
        testInfo = SDK43ServiceStyleSystemTestConstants.getTestServiceInfo();
        container = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
        File serviceDir = new File(testInfo.getDir());
        serviceDir.mkdirs();
        
        return container != null && serviceDir.exists() && serviceDir.isDirectory();
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new InstallStyleStep());
        steps.add(new CreateDataServiceStep(testInfo, getIntroduceBaseDir()));
        steps.add(new UnpackContainerStep(container));
        List<String> deploymentArgs = 
            Arrays.asList(new String[] {"-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
        steps.add(new DeployServiceStep(container, testInfo.getDir(), deploymentArgs));
        steps.add(new StartContainerStep(container));
        steps.add(new InvokeDataServiceStep(testInfo, container));
        return steps;
    }
    
    
    public void storyTearDown() throws Throwable {
        List<Throwable> errors = new LinkedList<Throwable>();
        try {
            new StopContainerStep(container).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            new DestroyContainerStep(container).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            Utils.deleteDir(new File(testInfo.getDir()));
        } catch (Throwable th) {
            errors.add(th);
        }
        
        if (errors.size() != 0) {
            System.err.println("EXCEPTION(S) OCCURED DURING TEAR DOWN:");
            for (Throwable t : errors) {
                System.err.println("----------------");
                t.printStackTrace();
            }
            throw new Exception("EXCEPTION(S) OCCURED DURING TEAR DOWN.  SEE LOGS");
        }
    }
    
    
    public String getIntroduceBaseDir() {
        String dir = System.getProperty(SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        assertNotNull("Introduce base dir system property " + 
            SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY + " is required", dir);
        return dir;
    }
}
