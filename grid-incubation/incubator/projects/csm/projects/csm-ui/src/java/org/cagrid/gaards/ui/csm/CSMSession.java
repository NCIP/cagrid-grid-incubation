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
