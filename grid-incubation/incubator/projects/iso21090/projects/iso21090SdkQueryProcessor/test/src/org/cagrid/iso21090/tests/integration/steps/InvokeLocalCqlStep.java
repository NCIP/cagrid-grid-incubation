package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InvokeLocalCqlStep extends Step {
    
    public static final String TESTS_BASEDIR_PROPERTY = "sdk43.tests.base.dir";
    public static final String TESTS_EXT_LIB_DIR = "ext/dependencies/jars";
    public static final String SDK_LOCAL_CLIENT_DIR = 
        "sdk/unpacked/sdk-toolkit/iso-example-project/target/dist/exploded/output/isoExample/package/local-client";
    
    public InvokeLocalCqlStep() {
        super();
    }


    public void runStep() throws Throwable {
        testLotsOfQueries();
    }
    
    
    private File[] getCqlQueryFiles() {
        File basedir = new File(System.getProperty(TESTS_BASEDIR_PROPERTY));
        File queriesDir = new File(basedir, "test/resources/testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                && !pathname.getName().startsWith("invalid_");
            }
        });
    }
    
    
    private File getGoldResultsFile(String queryFilename) {
        File basedir = new File(System.getProperty(TESTS_BASEDIR_PROPERTY));
        File goldDir = new File(basedir, "test/resources/testGoldResults");
        return new File(goldDir, "gold" + CommonTools.upperCaseFirstCharacter(queryFilename));
    }
    
    
    private void testLotsOfQueries() {
        CQL2ParameterizedHQL translator = getTranslator();
        ApplicationService service = getService();
        
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
    
    
    private CQL2ParameterizedHQL getTranslator() {
        ClassLoader loader = getSdkLibClassLoader();
        CQL2ParameterizedHQL translator = null;
        try {
            Class<?> typesInfoResolverClass = loader.loadClass("org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver");
            InputStream hbmConfigStream = typesInfoResolverClass.getResourceAsStream("/hibernate.cfg.xml");
            assertNotNull("Hibernate config was null", hbmConfigStream);
            Configuration hibernateConfig = new Configuration();
            hibernateConfig.addInputStream(hbmConfigStream);
            hibernateConfig.buildMapping();
            hibernateConfig.configure();
            hbmConfigStream.close();
            ApplicationContext isoContext = new ClassPathXmlApplicationContext("IsoConstants.xml", typesInfoResolverClass);
            translator = new CQL2ParameterizedHQL(
                new HibernateConfigTypesInformationResolver(hibernateConfig, true), 
                new IsoDatatypesConstantValueResolver(isoContext),
                false);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error: " + ex.getMessage());
        }
        
        return translator;
    }
    
    
    private ApplicationService getService() {
        ClassLoader loader = getSdkLibClassLoader();
        ApplicationService service = null;
        try {
            Class<?> providerClass = loader.loadClass("gov.nih.nci.system.client.ApplicationServiceProvider");
            Method getMethod = providerClass.getMethod("getApplicationService");
            service = (ApplicationService) getMethod.invoke(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error: " + ex.getMessage());
        }
        return service;
    }
    
    
    private ClassLoader getSdkLibClassLoader() {
        String base = System.getProperty(TESTS_BASEDIR_PROPERTY);
        assertNotNull(TESTS_BASEDIR_PROPERTY + " property was null", base);
        FileFilter jarfilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().toLowerCase().endsWith(".jar");
            }
        };
        File baseLibDir = new File(base, TESTS_EXT_LIB_DIR);
        System.out.println("Base lib dir = " + baseLibDir.getAbsolutePath());
        File[] baseLibs = baseLibDir.listFiles(jarfilter);
        File sdkLocalClientDir = new File(base, SDK_LOCAL_CLIENT_DIR);
        System.out.println("SDK local client dir = " + sdkLocalClientDir.getAbsolutePath());
        File sdkLibDir = new File(sdkLocalClientDir, "lib");
        System.out.println("sdkLibDir = " + sdkLibDir.getAbsoluteFile());
        File sdkConfDir = new File(sdkLocalClientDir, "conf");
        System.out.println("sdkConfDir = " + sdkConfDir.getAbsolutePath());
        File[] sdkLibs = sdkLibDir.listFiles(jarfilter);
        
        List<URL> libUrls = new ArrayList<URL>();
        try {
            libUrls.add(sdkConfDir.toURI().toURL());
            for (File lib : baseLibs) {
                libUrls.add(lib.toURI().toURL());
            }
            for (File lib : sdkLibs) {
                libUrls.add(lib.toURI().toURL());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error converting path to URL: " + ex.getMessage());
        }
        
        URL[] urls = libUrls.toArray(new URL[0]);
        ClassLoader loader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
        return loader;
    }
}
