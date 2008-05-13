package org.cagrid.tide.context.service;

import java.rmi.RemoteException;

import org.cagrid.tide.context.service.fetcher.WaveFetcher;
import org.cagrid.tide.service.TideConfiguration;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 */
public class TideContextImpl extends TideContextImplBase {

    private WaveFetcher fetcher = null; 
    
    public TideContextImpl() throws RemoteException {
        super();
        try {
            fetcher = (WaveFetcher)Class.forName(TideConfiguration.getConfiguration().getWaveFetcher()).newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if(fetcher == null){
            try {
                throw new RemoteException("Cannot load the fetcher class: " + TideConfiguration.getConfiguration().getWaveFetcher());
            } catch (Exception e) {
               throw new RemoteException("Cannot loat the TideConfiguration");
            }
        }
    }

  public org.cagrid.tide.descriptor.WaveDescriptor getWave(org.cagrid.tide.descriptor.WaveRequest waveRequest) throws RemoteException {
           try {
            return fetcher.getWave(waveRequest, getResourceHome().getAddressedResource().getTideDescriptor());
        } catch (Exception e) {
           throw new RemoteException("ERROR",e);
        }
  }

}
