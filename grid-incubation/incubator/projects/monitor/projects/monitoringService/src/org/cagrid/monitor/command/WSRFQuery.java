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
package org.cagrid.monitor.command;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataHostingResearchCenter;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;

import java.util.Collection;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.cagrid.monitor.db.Service;

public class WSRFQuery implements Command {
	private static Logger LOG = Logger.getLogger(WSRFQuery.class);
	
	Service service = null;
	
	public CommandResult run() {
		CommandResult commandResult = new CommandResult();
		commandResult.setReturnCode(1);
		try {
			EndpointReferenceType endpointReferenceType = new EndpointReferenceType();
			AttributedURI address = new AttributedURI(service.getEpr().toString());
			endpointReferenceType.setAddress(address );
			ServiceMetadata serviceMetaData = MetadataUtils.getServiceMetadata(endpointReferenceType);
			commandResult.setReturnCode(0);
			
			ServiceMetadataHostingResearchCenter serviceMetadataHostingResearchCenter = serviceMetaData.getHostingResearchCenter();
			ResearchCenter researchCenter = serviceMetadataHostingResearchCenter.getResearchCenter();

			LOG.error(researchCenter.getDisplayName());
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return commandResult;
	}
	
	public void setParameters(Collection<Parameter> parameters) {
		Object[] params = parameters.toArray();
		Parameter parameter = (Parameter) params[0];
		this.service = (Service) parameter.getValue();
	}
}
