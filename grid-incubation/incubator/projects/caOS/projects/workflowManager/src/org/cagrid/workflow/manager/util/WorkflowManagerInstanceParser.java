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
package org.cagrid.workflow.manager.util;


/** Operations a parser must implement so a string describing a workflow can be used by the WorkflowManager */
public interface WorkflowManagerInstanceParser {

	/**
	 * Parse an XML description of a workflow into the WorkflowManagerInstance descriptor
	 * @throws Exception 
	 * */
	public org.cagrid.workflow.manager.descriptor.WorkflowManagerInstanceDescriptor parseWorkflowDescriptor(String xmlWorkflowDescription) throws Exception;
	
}
