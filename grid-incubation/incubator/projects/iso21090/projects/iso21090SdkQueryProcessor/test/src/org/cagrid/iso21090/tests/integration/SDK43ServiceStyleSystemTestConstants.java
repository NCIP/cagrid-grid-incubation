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
package org.cagrid.iso21090.tests.integration;

import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  SDK43ServiceStyleSystemTestConstants
 *  Constants for the SDK 4.3 with ISO 21090 types Data Service style system tests
 * 
 * @author David Ervin
 */
public class SDK43ServiceStyleSystemTestConstants {
    // the service style's internal name
    public static final String STYLE_NAME = "caCORE SDK v 4.3";
    public static final String STYLE_VERSION = "1.3";
    
    // system property to locate the Introduce base directory
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    
    // system property to locate the SDK 4.3 data service style zip
    public static final String STYLE_ZIP_PROPERTY = "style.zip.location";

    // test service naming
    public static final String SERVICE_PACKAGE = "org.cagrid.sdkquery43.test";
    public static final String SERVICE_NAME = "TestSDK43WithIsoTypesStyleDataService";
    public static final String SERVICE_NAMESPACE = "http://" + SERVICE_PACKAGE + "/" + SERVICE_NAME;
    
    public static SDK43TestServiceInfo getTestServiceInfo() {
        String suffix = String.valueOf(System.currentTimeMillis());
        return new SDK43TestServiceInfo(suffix);
    }
    
    
    private static class SDK43TestServiceInfo extends DataTestCaseInfo {
        private String dirSuffix;
        
        public SDK43TestServiceInfo(String dirSuffix) {
            this.dirSuffix = dirSuffix;
        }
        
        
        public String getServiceDirName() {
            return SDK43ServiceStyleSystemTestConstants.SERVICE_NAME + "_" + dirSuffix;
        }


        public String getName() {
            return SDK43ServiceStyleSystemTestConstants.SERVICE_NAME;
        }


        public String getNamespace() {
            return SDK43ServiceStyleSystemTestConstants.SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return SDK43ServiceStyleSystemTestConstants.SERVICE_PACKAGE;
        }


        public String getExtensions() {
            return "cagrid_metadata," + super.getExtensions();
        }
    }
}
