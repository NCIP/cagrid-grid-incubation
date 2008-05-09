package org.cagrid.cql2.test;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.io.File;
import java.io.FileReader;

import org.cagrid.cql2.preview.processor.DomainModelUtils;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  DomainModelUtilsTestCase
 *  Tests functionality of the DomainModelUtils class
 * 
 * @author David Ervin
 * 
 * @created May 8, 2008 4:01:13 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.2 2007/03/01 Exp $ 
 */
public class DomainModelUtilsTestCase extends TestCase {
    public static final String RESOURCES_DIR = "resources";
    
    public static final String DOMAIN_MODEL_FILE = RESOURCES_DIR 
        + File.separator + "sdkExampleDomainModel.xml";
    
    private DomainModel model;
    
    public DomainModelUtilsTestCase() {
    }
    
    
    public void setUp() {
        try {
            File modelFile = new File(DOMAIN_MODEL_FILE);
            assertTrue("Domain model file did not exist", modelFile.exists());
            assertTrue("Domain model file could not be read", modelFile.canRead());
            FileReader reader = new FileReader(modelFile);
            model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing domain model: " + ex.getMessage());
        }
    }
    
    
    public void testGetClassReference() {
        UMLClassReference ref = DomainModelUtils.getClassReference(
            model, "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel.Calculator");
        assertNotNull("Class reference was null", ref);
        assertEquals("Unexpected class reference ID", "EAID_47763202_9509_42ae_BA5D_7F05FBA1C014", ref.getRefid());;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(DomainModelUtilsTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
