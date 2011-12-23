package org.cagrid.tide.service.ingestor;

import org.cagrid.tide.descriptor.TideDescriptor;
import org.cagrid.transfer.descriptor.DataStorageDescriptor;


public interface TideIngestor {

    public void ingest(TideDescriptor tide, DataStorageDescriptor desc) throws Exception;

}
