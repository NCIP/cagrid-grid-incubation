package systemTests.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class DeployWorkflowServicesTest extends ServiceStoryBase {

	@Override
	public String getDescription() {

		return "Deploy the WorkflowHelperService and each service used to compose the test workflow";
	}

	@Override
	protected Vector steps() {


		System.out.println("BEGIN DeployWorkflowServicesTest");


		Vector steps = new Vector();


		// Set workflow services' directories 
		String tests_basedir = System.getProperty("test.dir")+File.separatorChar;
		if( tests_basedir == null ){  
			System.err.println("ERROR: System property 'test.dir' not set");
			return null;
		}
		File services_dirs[] = new File[]{
				new File(tests_basedir+"Service1"),
				new File(tests_basedir+"Service2"),
				new File(tests_basedir+"Service3"), 
				new File(tests_basedir+"Service4"), 
				new File(tests_basedir+"Service5"),
				new File(tests_basedir+"ReceiveArrayService"), 
				new File(tests_basedir+"CreateArrayService"), 
				new File(tests_basedir+".."+ File.separatorChar +"workflowHelperService")
		}; 



		// Create container
		System.out.println("Adding unpack start task");
		steps.add(new UnpackContainerStep(getContainer()));




		// Deploy each service
		System.out.println("Adding deploy services task");
		for(int i=0; i < services_dirs.length; i++){
			String curr_service_dir = null;
			try {
				curr_service_dir = services_dirs[i].getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("Adding deploy for service in directory '"+ curr_service_dir +"'");
			steps.add(new DeployServiceStep(getContainer(), curr_service_dir));			
		}
		System.out.println("END DeployWorkflowServicesTest");

		return steps;
	}


	public boolean storySetUp(){


		System.out.println("Setting globus container for DeployWorkflowServicesTest");
		try {
			ServiceContainer container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
			setContainer(container);
			//getContainer().unpackContainer();
			//getContainer().stopContainer();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} /*catch (ContainerException e) {
			e.printStackTrace();
		} // */

		return true;
	}



	/*public void storyTearDown(){

		System.out.println("Stopping globus container at DeployWorkflowServicesTest");
		try {
			/*getContainer().unpackContainer();
			getContainer().stopContainer(); 
			getContainer().deleteContainer();
		} catch (ContainerException e) {
			e.printStackTrace();
		} 

	} // */


}
