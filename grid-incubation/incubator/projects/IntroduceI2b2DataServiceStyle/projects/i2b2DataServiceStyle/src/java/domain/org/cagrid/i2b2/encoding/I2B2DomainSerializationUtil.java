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

import gov.nih.nci.cagrid.common.Utils;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javax.xml.namespace.QName;

import org.cagrid.i2b2.domain.Concept;
import org.cagrid.i2b2.domain.Map;
import org.cagrid.i2b2.domain.MapData;
import org.cagrid.i2b2.domain.Observation;
import org.cagrid.i2b2.domain.Patient;
import org.cagrid.i2b2.domain.Provider;
import org.cagrid.i2b2.domain.Visit;

public class I2B2DomainSerializationUtil {
    public static final String CLIENT_CONFIG_LOCATION = "/org/cagrid/i2b2/domain/client-config.wsdd";
    public static final String I2B2_DOMAIN_URI = "http://triadcommunity.org/i2b2/domain";    
    public static java.util.Map<Class<?>, QName> I2B2_TYPE_QNAMES = null;
    static {
        HashMap<Class<?>, QName> names = new HashMap<Class<?>, QName>();
        names.put(Concept.class, new QName(I2B2_DOMAIN_URI, "Concept"));
        names.put(Map.class, new QName(I2B2_DOMAIN_URI, "Map"));
        names.put(MapData.class, new QName(I2B2_DOMAIN_URI, "MapData"));
        names.put(Observation.class, new QName(I2B2_DOMAIN_URI, "Observation"));
        names.put(Patient.class, new QName(I2B2_DOMAIN_URI, "Patient"));
        names.put(Provider.class, new QName(I2B2_DOMAIN_URI, "Provider"));
        names.put(Visit.class, new QName(I2B2_DOMAIN_URI, "Visit"));
        I2B2_TYPE_QNAMES = Collections.unmodifiableMap(names);
    }
    

    public static void serializeI2B2DomainObject(Object o, Writer writer) throws Exception {
        InputStream clientConfig = I2B2DomainSerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        QName name = I2B2_TYPE_QNAMES.get(o.getClass());
        Utils.serializeObject(o, name, writer, clientConfig);
        clientConfig.close();
    }
    
    
    public static Object deserializeI2B2DomainObject(Reader reader, Class<?> clazz) throws Exception {
        InputStream clientConfig = I2B2DomainSerializationUtil.class.getResourceAsStream(CLIENT_CONFIG_LOCATION);
        Object o = Utils.deserializeObject(reader, clazz, clientConfig);
        clientConfig.close();
        return o;
    }
    
    
    public static void main(String[] args) {
        try {
            Concept c = new Concept();
            c.setCdePublicId(Double.valueOf(1234.5));
            c.setDownloadDate(new Date());
            
            StringWriter writer = new StringWriter();
            serializeI2B2DomainObject(c, writer);
            System.out.println(writer.getBuffer().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
