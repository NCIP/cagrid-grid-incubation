package org.cagrid.i2b2.test.utils;

import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.i2b2.ontomapper.utils.AttributeNotFoundInModelException;
import org.cagrid.i2b2.ontomapper.utils.ClassNotFoundInModelException;
import org.cagrid.i2b2.ontomapper.utils.DomainModelConceptCodeMapper;

/**
 * DomainModelConceptCodeMapperTestCase
 * Tests the DomainModelConceptCodeMapper
 * 
 * @author David
 */
public class DomainModelConceptCodeMapperTestCase extends TestCase {
    
    public static final String MODEL_LOCATION = "/test/resources/aim_v1_DomainModel.xml";
    
    private DomainModelConceptCodeMapper mapper = null;
    
    public DomainModelConceptCodeMapperTestCase() {
        super("Domain Model Concept Code Mapper Test Case");
    }
    
    
    public void setUp() {
        try {
            InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(MODEL_LOCATION));
            DomainModel model = MetadataUtils.deserializeDomainModel(reader);
            reader.close();
            mapper = new DomainModelConceptCodeMapper(model);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up domain model concept code mapper: " + ex.getMessage());
        }
    }
    
    
    public void testClassConceptCodes() {
        String className = "edu.northwestern.radiology.AIM.MultiPoint";
        List<String> expectedCodes = Arrays.asList(new String[] {
            "C48046", "C70656", "C17648"
        });
        List<String> foundCodes = null;
        try {
            foundCodes = mapper.getConceptCodesForClass(className);
        } catch (ClassNotFoundInModelException ex) {
            ex.printStackTrace();
            fail("Class " + className + " was expected to be in the model: " + ex.getMessage());
        }
        assertEquals("Unexpected number of concept codes found", expectedCodes.size(), foundCodes.size());
        Collections.sort(expectedCodes);
        Collections.sort(foundCodes);
        for (int i = 0; i < expectedCodes.size(); i++) {
            assertEquals("Unexpected code found", expectedCodes.get(i), foundCodes.get(i));
        }
    }
    
    
    public void testAttributeConceptCodes() {
        String className = "edu.northwestern.radiology.AIM.MultiPoint";
        String attributeName = "lineColor";
        List<String> expectedCodes = Arrays.asList(new String[] {
            "C37927", "C71604"
        });
        List<String> foundCodes = null;
        try {
            foundCodes = mapper.getConceptCodesForAttribute(className, attributeName);
        } catch (ClassNotFoundInModelException ex) {
            ex.printStackTrace();
            fail("Class " + className + " was expected to be in the model: " + ex.getMessage());
        } catch (AttributeNotFoundInModelException ex) {
            ex.printStackTrace();
            fail("Attribute " + attributeName + " was expected to be in the model: " + ex.getMessage());
        }
        assertEquals("Unexpected number of concept codes found", expectedCodes.size(), foundCodes.size());
        Collections.sort(expectedCodes);
        Collections.sort(foundCodes);
        for (int i = 0; i < expectedCodes.size(); i++) {
            assertEquals("Unexpected code found", expectedCodes.get(i), foundCodes.get(i));
        }
    }
    
    
    public void testInvalidClass() {
        String className = "non.existant.package.AndClass";
        try {
            mapper.getConceptCodesForClass(className);
        } catch (ClassNotFoundInModelException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    
    public void testValidClassInvalidAttribute() {
        String className = "edu.northwestern.radiology.AIM.MultiPoint";
        String attributeName = "nonExistantAttribute";
        try {
            mapper.getConceptCodesForAttribute(className, attributeName);
        } catch (AttributeNotFoundInModelException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    
    
    public void testInvalidClassInvalidAttribute() {
        String className = "non.existant.package.AndClass";
        String attributeName = "nonExistantAttribute";
        try {
            mapper.getConceptCodesForAttribute(className, attributeName);
        } catch (ClassNotFoundInModelException ex) {
            // expected... should throw this before attribute not found
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown: " + ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(DomainModelConceptCodeMapperTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
