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
package org.cagrid.i2b2.encoding;

import gov.nih.nci.cagrid.encoding.AxisContentHandler;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

public class I2B2Serializer implements Serializer {
    
    private static Log LOG = LogFactory.getLog(I2B2Serializer.class);

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
        throws IOException {
        long start = System.currentTimeMillis();
        
        // load the mapping
        Mapping map = new Mapping();
        map.loadMapping(new InputSource(I2B2DomainSerialzationHelper.getMappingStream()));
        
        AxisContentHandler hand = new AxisContentHandler(context);
        Marshaller marshaller = new Marshaller(hand);
        try {
            marshaller.setMapping(map);
        } catch (MappingException ex) {
            String error = "Error setting I2B2 domain castor mapping: " + ex.getMessage();
            LOG.error(error, ex);
            IOException ioe = new IOException(error);
            ioe.initCause(ex);
            throw ioe;
        }
        // TODO: evaluate if I need this, and if so, why
        marshaller.setSuppressXSIType(true);
        marshaller.setValidation(true);
        
        try {
            marshaller.marshal(value);
        } catch (MarshalException e) {
            String message = "Problem using castor marshalling for I2B2 domain: " + e.getMessage();
            LOG.error(message, e);
            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        } catch (ValidationException e) {
            String message = "Problem validating castor marshalling; " +
                "message doesn't comply with the associated XML schema: " + e.getMessage();
            LOG.error(message, e);
            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }
        
        LOG.trace("Serialized " + name.getLocalPart() + " in " + (System.currentTimeMillis() - start) + " ms");
    }


    @SuppressWarnings("unchecked")
    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }


    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }
}
