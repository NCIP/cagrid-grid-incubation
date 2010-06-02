package org.cagrid.iso21090.sdkquery.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;


public class CQLInvocationTestCase extends TestCase {

    private ApplicationService service = null;
    private CQL2ParameterizedHQL translator = null;


    public CQLInvocationTestCase(String name) {
        super(name);
        // gets the local SDK service instance
        try {
            service = QueryTestsHelper.getSdkApplicationService();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining application service instance: " + ex.getMessage());
        }

        try {
            translator = QueryTestsHelper.getCqlTranslator();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


    private File[] getCqlQueryFiles() {
        File queriesDir = new File("test/resources/testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                    && !pathname.getName().startsWith("invalid_") 
                    && !pathname.getName().startsWith("dsetAd");
            }
        });
    }


    private File getGoldResultsFile(String queryFilename) {
        File goldDir = new File("test/resources/testGoldResults");
        return new File(goldDir, "gold" + CommonTools.upperCaseFirstCharacter(queryFilename));
    }


    public void testLotsOfQueries() {
        File[] queryFiles = getCqlQueryFiles();
        List<Exception> failures = new ArrayList<Exception>();
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
                String message = "Error translating query " + f.getName() + ": " + ex.getMessage();
                System.err.println(message);
                Exception fail = new Exception(message, ex);
                failures.add(fail);
                continue;
            }
            System.out.println("Translated query:");
            System.out.println(hql);
            List<?> results = null;
            try {
                results = service.query(new HQLCriteria(hql.getHql(), hql.getParameters()));
            } catch (Exception ex) {
                String message = "Error executing query " + f.getName() + ": " + ex.getMessage();
                System.err.println(message);
                Exception fail = new Exception(message, ex);
                failures.add(fail);
                continue;
            }
            // TODO: load up gold results, validate
        }
        for (Exception ex : failures) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        assertEquals("Some queries failed", 0, failures.size());
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CQLInvocationTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
