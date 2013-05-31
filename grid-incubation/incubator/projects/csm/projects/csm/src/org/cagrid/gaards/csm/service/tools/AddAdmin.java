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
package org.cagrid.gaards.csm.service.tools;

import gov.nih.nci.security.AuthorizationManager;

import org.cagrid.gaards.csm.service.BeanUtils;
import org.cagrid.gaards.csm.service.CSMInitializer;
import org.cagrid.gaards.csm.service.CSMProperties;
import org.springframework.core.io.FileSystemResource;


public class AddAdmin {

    public static void main(String[] args) {
        try {
            String confFile = args[0];
            String propsFile = args[1];
            String identity = args[2];
            BeanUtils utils = new BeanUtils(new FileSystemResource(confFile), new FileSystemResource(propsFile));
            CSMProperties properties = utils.getCSMProperties();
            AuthorizationManager am = CSMInitializer.getAuthorizationManager(properties.getDatabaseProperties());
            CSMInitializer.addWebServiceAdmin(am, identity);
            System.out.println("Successfully added " + identity + " as an administrator of the CSM Web Service.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
