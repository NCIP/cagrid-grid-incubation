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
package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NukeIvyCacheStep extends Step {
    
    private static Log LOG = LogFactory.getLog(NukeIvyCacheStep.class);
    
    public NukeIvyCacheStep() {
        super();
    }
    

    public void runStep() throws Throwable {
        File homeDir = new File(System.getProperty("user.home"));
        if (homeDir.exists()) {
            File bdaCache = new File(homeDir, ".ivy2-bda-utils");
            if (bdaCache.exists()) {
                LOG.info("Deleting BDA utils ivy cache at " + bdaCache.getAbsolutePath());
                Utils.deleteDir(bdaCache);
            } else {
                LOG.info("BDA utils ivy cache " + bdaCache.getAbsolutePath() + " not found");
            }
            File sdkCache = new File(homeDir, ".ivy2-sdk");
            if (sdkCache.exists()) {
                LOG.info("Deleting sdk ivy cache at " + sdkCache.getAbsolutePath());
                Utils.deleteDir(sdkCache);
            } else {
                LOG.info("SDK ivy cache " + sdkCache.getAbsolutePath() + " not found");
            }
        } else {
            LOG.warn("User home dir " + homeDir.getAbsolutePath() + " not found");
        }
    }
}
