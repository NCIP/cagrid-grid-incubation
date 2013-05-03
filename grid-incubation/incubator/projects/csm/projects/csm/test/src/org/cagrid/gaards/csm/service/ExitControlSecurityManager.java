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
package org.cagrid.gaards.csm.service;

import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.util.optional.NoExitSecurityManager;

class ExitControlSecurityManager extends NoExitSecurityManager {
    boolean exitPermitted = false;
    
    /* (non-Javadoc)
     * @see java.lang.SecurityManager#checkExit(int)
     */
    @Override
    public void checkExit(int status) {
        if (!exitPermitted) {
            throw new ExitException(status);
        }
    }        
}
