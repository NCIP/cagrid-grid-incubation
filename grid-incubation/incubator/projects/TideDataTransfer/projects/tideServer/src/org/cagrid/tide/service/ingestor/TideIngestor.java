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
package org.cagrid.tide.service.ingestor;

import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;


public interface TideIngestor {

    public void ingest(TideDescriptor tide, DataStorageDescriptor desc) throws Exception;

}
