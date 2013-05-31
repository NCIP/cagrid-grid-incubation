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
package org.cagrid.tide.service.ingestor;

import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;


public interface TideIngestor {

    public void ingest(TideDescriptor tide, DataStorageDescriptor desc) throws Exception;

}
