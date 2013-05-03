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
package org.cagrid.tide.context.service.fetcher;

import org.cagrid.tide.descriptor.TideDescriptor;


public interface WaveFetcher {
    public org.cagrid.tide.descriptor.WaveDescriptor getWave(org.cagrid.tide.descriptor.WaveRequest waveRequest,
        TideDescriptor tideDescriptor) throws Exception;

}
