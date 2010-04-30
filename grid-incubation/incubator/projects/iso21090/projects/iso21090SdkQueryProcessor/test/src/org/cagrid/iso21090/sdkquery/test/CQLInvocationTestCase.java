package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.ConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationResolver;
import org.hibernate.cfg.Configuration;

public class CQLInvocationTestCase extends TestCase {
    
    private ApplicationService service = null;
    private CQL2ParameterizedHQL translator = null;
    
    public CQLInvocationTestCase(String name) {
        super(name);
        long start = System.currentTimeMillis();
        // gets the local SDK service instance
        try {
            service = ApplicationServiceProvider.getApplicationService();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining application service instance: " + ex.getMessage());
        }
        System.out.println("Application service initialized in " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        InputStream is = getClass().getResourceAsStream("/hibernate.cfg.xml");
        Configuration config = new Configuration();
        config.addInputStream(is);
        config.buildMappings();
        config.configure();
        TypesInformationResolver typesInfoResolver = new HibernateConfigTypesInformationResolver(config, true);
        try {
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error closing hibernate configuration input stream: " + ex.getMessage());
        }
        System.out.println("Types information resolver initialized in " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        ConstantValueResolver constResolver = new IsoDatatypesConstantValueResolver();
        System.out.println("Constant value resolver initialized in " + (System.currentTimeMillis() - start));
        
        translator = new CQL2ParameterizedHQL(typesInfoResolver, constResolver, false);
    }
    
    
    private File[] getCqlQueryFiles() {
        File queriesDir = new File("test/resources/testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                && !pathname.getName().startsWith("invalid_");
            }
        });
    }
    
    
    private File getGoldResultsFile(String queryFilename) {
        File goldDir = new File("test/resources/testGoldResults");
        return new File(goldDir, "gold" + CommonTools.upperCaseFirstCharacter(queryFilename));
    }
    
    
    public void testLotsOfQueries() {
        File[] queryFiles = getCqlQueryFiles();
        System.out.println("Found " + queryFiles.length + " query documents to run");
        for (File f : queryFiles) {
            System.out.println("Loading " + f.getName());
            CQLQuery query = null;
            try {
                query = (CQLQuery) Utils.deserializeDocument(f.getAbsolutePath(), CQLQuery.class);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error loading query: " + ex.getMessage());
            }
            System.out.println("Translating");
            ParameterizedHqlQuery hql = null;
            try {
                hql = translator.convertToHql(query);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error translating query: " + ex.getMessage());
            }
            System.out.println("Translated query:");
            System.out.println(hql);
            List<?> results = null;
            try {
                results = service.query(new HQLCriteria(hql.getHql(), hql.getParameters()));
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error executing query: " + ex.getMessage());
            }
            // TODO: load up gold results, validate
        }
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQLInvocationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
