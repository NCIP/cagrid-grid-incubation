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
