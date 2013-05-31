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
package org.cagrid.workflow.helper.test.unit;

import junit.framework.TestCase;

public class WorkflowHelperServiceImplTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	// This test needs a globus container running. So, we will test it in the system tests, not here
	// TODO Maybe Mock objects can help us to implement this unit test
	public void testCreateWorkflowHelperInstance() {
		
		
		/*WorkflowHelperServiceImpl helper = null;
		try {
			helper = new WorkflowHelperServiceImpl();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Couldn't instantiate a Helper");
		}
		
		
		org.cagrid.workflow.helper.descriptor.WorkflowHelperInstanceDescriptor workflowDescriptor = new org.cagrid.workflow.helper.descriptor.WorkflowHelperInstanceDescriptor();

		java.lang.String access_url = "http://localhost:8080/wsrf/services/cagrid/Service4";
		workflowDescriptor.setWorkflowID("GeorgeliusWorkFlow");
		workflowDescriptor.setOperationQName(new QName("http://service4.introduce.cagrid.org/Service4", "PrintResultsRequest"));
		workflowDescriptor.setServiceURL(access_url);

		WorkflowHelperInstanceServiceReference ref = null;
		try {
			 ref = helper.createWorkflowHelperInstance(workflowDescriptor);
			 EndpointReferenceType epr1 = ref.getEndpointReference();
			 EndpointReferenceType epr2 = new EndpointReferenceType(new Address(access_url));
			 if( !epr1.equals(epr2) ){
				 fail("EndpointReference not created properly");
			 }
			 
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Couldn't instantiate a HelperInstance");
		} catch (MalformedURIException e) {
			e.printStackTrace();
			fail("Couldn't instantiate a HelperInstance");
		} // */
		
	}

}
