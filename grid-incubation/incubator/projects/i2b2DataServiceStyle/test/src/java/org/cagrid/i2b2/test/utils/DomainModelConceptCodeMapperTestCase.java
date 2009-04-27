package org.cagrid.i2b2.test.utils;

import org.cagrid.i2b2.ontomapper.utils.DomainModelConceptCodeMapper;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * DomainModelConceptCodeMapperTestCase
 * Tests the DomainModelConceptCodeMapper
 * 
 * @author David
 */
public class DomainModelConceptCodeMapperTestCase extends TestCase {
    
    public static final String MODEL_LOCATION = "test/resources/aim_v1_DomainModel.xml";
    
    private DomainModelConceptCodeMapper mapper = null;
    
    public DomainModelConceptCodeMapperTestCase() {
        super("Domain Model Concept Code Mapper Test Case");
    }
    
    
    public void setUp() {
        
    }
    
    
    
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(DomainModelConceptCodeMapperTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
