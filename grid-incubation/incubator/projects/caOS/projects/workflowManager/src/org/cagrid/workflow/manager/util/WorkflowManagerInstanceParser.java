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
package org.cagrid.workflow.manager.util;


/** Operations a parser must implement so a string describing a workflow can be used by the WorkflowManager */
public interface WorkflowManagerInstanceParser {

	/**
	 * Parse an XML description of a workflow into the WorkflowManagerInstance descriptor
	 * @throws Exception 
	 * */
	public org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor parseWorkflowDescriptor(String xmlWorkflowDescription) throws Exception;
	
}
