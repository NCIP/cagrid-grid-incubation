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
package org.cagrid.i2b2.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class I2B2DomainSerialzationHelper {

    public static final String MAPPING_LOCATION = "/org/cagrid/i2b2/domain/i2b2-domain-castor-mapping.xml";
    
    private static Log LOG = LogFactory.getLog(I2B2DomainSerialzationHelper.class);
    
    private static byte[] mappingBytes = null;
    
    static synchronized ByteArrayInputStream getMappingStream() throws IOException {
        if (mappingBytes == null) {
            LOG.debug("Reading I2B2 domain castor mapping from " + MAPPING_LOCATION);
            InputStream rawStream = I2B2DomainSerialzationHelper.class.getResourceAsStream(MAPPING_LOCATION);
            if (rawStream == null) {
                LOG.error("Mapping not found!");
            }
            ByteArrayOutputStream byteHolder = new ByteArrayOutputStream();
            byte[] temp = new byte[8192];
            int read = -1;
            while ((read = rawStream.read(temp)) != -1) {
                byteHolder.write(temp, 0, read);
            }
            byteHolder.flush();
            byteHolder.close();
            rawStream.close();
            mappingBytes = byteHolder.toByteArray();
        }
        return new ByteArrayInputStream(mappingBytes);
    }
}
