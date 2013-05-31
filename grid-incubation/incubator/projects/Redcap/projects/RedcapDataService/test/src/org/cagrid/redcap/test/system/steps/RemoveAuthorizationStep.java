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
package org.cagrid.redcap.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.redcap.test.system.RCDSTestCaseInfo;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.security.AnonymousCommunication;
import gov.nih.nci.cagrid.introduce.beans.security.CommunicationMethod;
import gov.nih.nci.cagrid.introduce.beans.security.IntroducePDPAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.NoAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.RunAsMode;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.ServiceSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.TransportLevelSecurity;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * RemoveAuthorizationStep
 * To remove the Grid Grouper Authorization details from introduce.xml 
 * and re-sync the service skeleton
 * 
 */
public class RemoveAuthorizationStep extends Step{
	
	private String serviceDirectory;
	private RCDSTestCaseInfo tci;
	
	private static final Log LOG = LogFactory.getLog(RemoveAuthorizationStep.class);
	
	public RemoveAuthorizationStep(String serviceDirectory, RCDSTestCaseInfo tci) {
		super();
		this.serviceDirectory = serviceDirectory;
		this.tci = tci;
	}

	@Override
	public void runStep() throws Throwable {
              
        ServiceDescription introService = (ServiceDescription) Utils.deserializeDocument("C:\\MyWorkspace-Incubator\\RedcapDataService\\introduce.xml"
                , ServiceDescription.class);
        
        ServiceSecurity serviceSecurity = new ServiceSecurity();
        serviceSecurity.setSecuritySetting(SecuritySetting.Custom);
        
        TransportLevelSecurity transportLevelSecurity = new TransportLevelSecurity(CommunicationMethod.Privacy);
        serviceSecurity.setTransportLevelSecurity(transportLevelSecurity);
        serviceSecurity.setRunAsMode(RunAsMode.System);
        serviceSecurity.setAnonymousClients(AnonymousCommunication.No);
        
        ServiceAuthorization serviceAuth = new ServiceAuthorization();
        NoAuthorization noAuth = new NoAuthorization();
        serviceAuth.setNoAuthorization(noAuth);
        
        IntroducePDPAuthorization introducePDPAuthorization = null; 
        serviceAuth.setIntroducePDPAuthorization(introducePDPAuthorization);
        
        serviceSecurity.setServiceAuthorization(serviceAuth);

        ExtensionsType extensions = new ExtensionsType();
        
		CommonTools.getService(introService.getServices(),tci.getName()).setServiceSecurity(serviceSecurity);
		CommonTools.getService(introService.getServices(),tci.getName()).setExtensions(extensions);

		Utils.serializeDocument(tci.getTempDir()+File.separator+"introduce.xml",
		            introService, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
        		
        try {
        	SyncTools sync = new SyncTools(new File(serviceDirectory ));
        	sync.sync();
        } catch (Exception e) {
            LOG.error("Error performing sync",e);
            fail(e.getMessage());
        }
		
	}
}
