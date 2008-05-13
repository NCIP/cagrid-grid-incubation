package org.cagrid.tide.context.service.fetcher;

import org.cagrid.tide.descriptor.TideDescriptor;


public interface WaveFetcher {
    public org.cagrid.tide.descriptor.WaveDescriptor getWave(org.cagrid.tide.descriptor.WaveRequest waveRequest,
        TideDescriptor tideDescriptor) throws Exception;

}
