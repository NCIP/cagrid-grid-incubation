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
