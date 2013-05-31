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
package org.cagrid.redcap.cql.test;

import gov.nih.nci.cagrid.common.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.cagrid.redcap.Forms;
import org.cagrid.redcap.FormsPk;
import org.cagrid.redcap.client.RedcapDataServiceClient;

public class DeserializeBeansTestCase extends TestCase {
    
    public DeserializeBeansTestCase(String name) {
        super(name);
    }
    
    
    public void testDeserializeForm() {
        InputStream wsdd = RedcapDataServiceClient.class.getResourceAsStream("client-config.wsdd");
        
        FormsPk pk = new FormsPk();
        pk.setFieldName("myFieldName");
        pk.setProjectId(123);
        Forms f = new Forms();
        f.setBranchingLogic("foo");
        f.setElementEnum("bar");
        f.setElementType("wtf");
        f.setFieldUnits("lol");
        f.setPk(pk);
        
        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(f, new QName("http://redcap.cagrid.org/Redcap", "forms"), writer, wsdd);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        String xml = writer.getBuffer().toString();
        System.out.println(xml);
        try {
            wsdd.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        wsdd = RedcapDataServiceClient.class.getResourceAsStream("client-config.wsdd");
        Forms f2 = null;
        try {
            f2 = (Forms) Utils.deserializeObject(new StringReader(xml), Forms.class, wsdd);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        assertEquals(f, f2);
    }
    
    

    public static void main(String[] args) {
        
    }
}
