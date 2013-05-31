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
package org.cagrid.gaards.ui.csm;

import org.cagrid.gaards.csm.client.CSM;
import org.globus.gsi.GlobusCredential;


public class CSMSession {
    private CSMHandle handle;

    private GlobusCredential credential;


    public CSMSession(CSMHandle handle) {
        this(handle, null);
    }


    public CSMSession(CSMHandle handle, GlobusCredential credential) {
        this.handle = handle;
        this.credential = credential;
    }


    public CSM getCSM() throws Exception {
        return handle.getClient(this.credential);
    }


    public CSMHandle getHandle() {
        return handle;
    }

}
