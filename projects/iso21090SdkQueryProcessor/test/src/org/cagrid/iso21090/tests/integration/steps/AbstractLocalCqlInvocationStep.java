package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLocalCqlInvocationStep extends Step {

    public static final String TESTS_BASEDIR_PROPERTY = "sdk43.tests.base.dir";
    public static final String TESTS_EXT_LIB_DIR = 
        "ext" + File.separator + "dependencies" + File.separator + "jars";
    public static final String SDK_LOCAL_CLIENT_DIR = 
        "sdk" + File.separator + "unpacked" + File.separator + "sdk-toolkit" + 
        File.separator + "iso-example-project" + File.separator + "target" + 
        File.separator + "dist" + File.separator + "exploded" + File.separator +
        "output" + File.separator + "isoExample" + File.separator + "package" + 
        File.separator + "local-client";
    
    private ApplicationService service = null;
    
    public AbstractLocalCqlInvocationStep() {
        super();
    }
    
    
    public void runStep() throws Throwable {
        testLotsOfQueries();
    }
    
    
    private File[] getCqlQueryFiles() {
        File basedir = new File(System.getProperty(TESTS_BASEDIR_PROPERTY));
        File queriesDir = new File(basedir, 
            "test" + File.separator + "resources" + File.separator + "testQueries");
        return queriesDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".xml")
                && !pathname.getName().startsWith("invalid_");
            }
        });
    }
    
    
    private void testLotsOfQueries() {
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
            System.out.println("Executing query " + f.getName());
            List<?> results = null;
            try {
                results = executeQuery(query);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error executing query " + f.getName() + ": " + ex.getMessage());
            }
            // TODO: load up gold results, validate
        }
    }
    
    
    protected abstract List<?> executeQuery(CQLQuery query) throws Exception;
    
    
    protected ApplicationService getService() {
        if (service == null) {
            ClassLoader loader = getSdkLibClassLoader();
            try {
                Class<?> providerClass = loader.loadClass("gov.nih.nci.system.client.ApplicationServiceProvider");
                Method getMethod = providerClass.getMethod("getApplicationService");
                service = (ApplicationService) getMethod.invoke(null);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error: " + ex.getMessage());
            }
        }
        return service;
    }
    
    
    protected ClassLoader getSdkLibClassLoader() {
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
