package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.cagrid.iso21090.sdkquery.translator.CQL2ParameterizedHQL;
import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.IsoDatatypesConstantValueResolver;
import org.cagrid.iso21090.sdkquery.translator.ParameterizedHqlQuery;
import org.hibernate.cfg.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class InvokeLocalTranslatedCqlStep extends AbstractLocalCqlInvocationStep {
    
    private CQL2ParameterizedHQL translator = null;
    
    public InvokeLocalTranslatedCqlStep() {
        super();
    }
    
    
    private CQL2ParameterizedHQL getTranslator() {
        if (translator == null) {
            ClassLoader loader = getSdkLibClassLoader();
            try {
                Class<?> typesInfoResolverClass = loader.loadClass("org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver");
                InputStream hbmConfigStream = typesInfoResolverClass.getResourceAsStream("/hibernate.cfg.xml");
                assertNotNull("Hibernate config was null", hbmConfigStream);
                Configuration hibernateConfig = new Configuration();
                hibernateConfig.addInputStream(hbmConfigStream);
                hibernateConfig.buildMapping();
                hibernateConfig.configure();
                hbmConfigStream.close();

                String base = System.getProperty(TESTS_BASEDIR_PROPERTY);
                File sdkLocalClientDir = new File(base, SDK_LOCAL_CLIENT_DIR);
                File sdkConfDir = new File(sdkLocalClientDir, "conf");
                ApplicationContext isoContext = new FileSystemXmlApplicationContext(new File(sdkConfDir, "IsoConstants.xml").getAbsolutePath());
                translator = new CQL2ParameterizedHQL(
                    new HibernateConfigTypesInformationResolver(hibernateConfig, true), 
                    new IsoDatatypesConstantValueResolver(isoContext),
                    false);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error: " + ex.getMessage());
            }
        }
        
        return translator;
    }


    protected List<?> executeQuery(CQLQuery query) throws Exception {
        ParameterizedHqlQuery translated = getTranslator().convertToHql(query);
        return getService().query(new HQLCriteria(translated.getHql(), translated.getParameters()));
    }
}
