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
package org.cagrid.cql2.test;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.cql2.preview.processor.DomainModelUtils;

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
    
    public static final String CALCULATOR_DOMAIN_CLASS_ID = "EAID_47763202_9509_42ae_BA5D_7F05FBA1C014";
    public static final String CALCULATOR_DOMAIN_PACKAGE = "gov.nih.nci.cacoresdk.domain.inheritance.twolevelinheritance.sametablerootlevel";
    public static final String CALCULATOR_DOMAIN_CLASS = CALCULATOR_DOMAIN_PACKAGE + ".Calculator";
    public static final String SCIENTIFIC_CALCULATOR_DOMAIN_CLASS = CALCULATOR_DOMAIN_PACKAGE + ".ScientificCalculator";
    public static final String GRAPHIC_CALCULATOR_DOMAIN_CLASS = CALCULATOR_DOMAIN_PACKAGE + ".GraphicCalculator";
    
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
            model, CALCULATOR_DOMAIN_CLASS);
        assertNotNull("Class reference was null", ref);
        assertEquals("Unexpected class reference ID", CALCULATOR_DOMAIN_CLASS_ID, ref.getRefid());
    }
    
    
    public void testGetReferencedClass() {
        UMLClassReference ref = new UMLClassReference();
        ref.setRefid(CALCULATOR_DOMAIN_CLASS_ID);
        UMLClass clazz = DomainModelUtils.getReferencedUMLClass(model, ref);
        assertNotNull("Dereferenced class was null", clazz);
        assertEquals("Unexpected class was resolved", 
            CALCULATOR_DOMAIN_CLASS, DomainModelUtils.getQualifiedClassname(clazz));
    }
    
    
    public void testGetSuperclasses() {
        UMLClass[] classes = DomainModelUtils.getAllSuperclasses(model, GRAPHIC_CALCULATOR_DOMAIN_CLASS);
        assertNotNull("No superclasses found", classes);
        assertTrue("No classes found", classes.length != 0);
        Set<String> superClassNames = new HashSet<String>();
        for (UMLClass c : classes) {
            superClassNames.add(DomainModelUtils.getQualifiedClassname(c));
        }
        boolean scientificCalcFound = superClassNames.remove(SCIENTIFIC_CALCULATOR_DOMAIN_CLASS);
        boolean calcFound = superClassNames.remove(CALCULATOR_DOMAIN_CLASS);
        assertTrue(SCIENTIFIC_CALCULATOR_DOMAIN_CLASS + " not found as superclass of " + GRAPHIC_CALCULATOR_DOMAIN_CLASS, scientificCalcFound);
        assertTrue(CALCULATOR_DOMAIN_CLASS + " not found as superclass of " + GRAPHIC_CALCULATOR_DOMAIN_CLASS, calcFound);
        StringBuffer message = new StringBuffer();
        message.append("Found unexpected superclasses:\n");
        for (String s : superClassNames) {
            message.append(s).append("\n");
        }
        assertTrue(message.toString(), superClassNames.size() == 0);
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(DomainModelUtilsTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
