/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
/**
 */
package edu.emory.cci.cqlCsm.cqlCsmHibernate;

//import gov.nih.nci.cagrid.sdkquery4.processor.ParameterizedHqlQuery;
import edu.emory.cci.cqlCsm.CsmProperties;
import edu.emory.cci.cqlCsm.SessionFactoryConfigurator;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.proxy.ListProxy;
import gov.nih.nci.system.dao.DAO;
import gov.nih.nci.system.dao.Request;
import gov.nih.nci.system.dao.Response;
import gov.nih.nci.system.dao.orm.translator.gridCQL.RoleNameResolver;
import gov.nih.nci.system.query.cql.CQLQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;
import gov.nih.nci.system.util.ClassCache;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.globus.wsrf.security.SecurityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.impl.CriteriaImpl;

/**
 * Implementation for the methods in the service layer
 * 
 * @author Satish Patel
 * @version 1.0
 */

public class ApplicationServiceImpl implements ApplicationService {

    private ClassCache classCache;

    private Integer maxRecordCount = 1000;

    private static Logger myLogger = Logger.getLogger(ApplicationServiceImpl.class.getName());

    private SessionFactory sessionFactory;

    private CsmProperties csmProperties;

    /**
     * Default constructor. Cache is required and is expected to have a
     * collection of DAO System properties is also a required parameter used to
     * determine system specific parameters
     * 
     */
    public ApplicationServiceImpl(ClassCache classCache) {
        this.classCache = classCache;
        csmProperties = new CsmProperties();
        sessionFactory = SessionFactoryConfigurator.getCsmAndAppSessionFactory(csmProperties.getAppName());
        // List<DAO> daoList = classCache.getDaoList();
        // if (daoList != null && daoList.size() > 0) {
        // DAO dao = daoList.get(0);
        // if (dao instanceof ORMDAOImpl) {
        // ORMDAOImpl ormDao = (ORMDAOImpl) dao;
        // maxRecordCount = ormDao.getResultCountPerQuery();
        // SecurityInitializationHelper securityHelper =
        // ormDao.getSecurityHelper();
        // if (securityHelper == null) {
        // String msg = "securityHelper is null!";
        // myLogger.error(msg);
        // throw new NullPointerException(msg);
        // }
        // AuthorizationManager authorizationManager =
        // securityHelper.getAuthorizationManager();
        // if (authorizationManager == null) {
        // String msg = "authorizationManager is null!";
        // myLogger.error(msg);
        // throw new NullPointerException(msg);
        // }
        // app = (Application) authorizationManager.getApplicationContext();
        // sessionFactory =
        // SessionFactoryConfigurator.getCsmAndAppSessionFactory(app.getApplicationName());
        // }
        // } else {
        // throw new
        // RuntimeException("ClassCache does not have a DAO list that begins with an instance of ORMDAOImpl");
        // }
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(org.hibernate.criterion.DetachedCriteria,
     *      java.lang.String)
     */
    public List<Object> query(DetachedCriteria detachedCriteria, String targetClassName) throws ApplicationException {
        return query(detachedCriteria);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(org.hibernate.criterion.DetachedCriteria)
     */
    public List<Object> query(DetachedCriteria detachedCriteria) throws ApplicationException {
        CriteriaImpl crit = (CriteriaImpl) detachedCriteria.getExecutableCriteria(null);
        String targetClassName = crit.getEntityOrClassName();
        return privateQuery(detachedCriteria, targetClassName);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(gov.nih.nci.system.query.hibernate.HQLCriteria,
     *      java.lang.String)
     */
    public List<Object> query(HQLCriteria hqlCriteria, String targetClassName) throws ApplicationException {
        throw new UnsupportedOperationException();
        // return query(hqlCriteria);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(gov.nih.nci.system.query.hibernate.HQLCriteria)
     */
    public List<Object> query(HQLCriteria hqlCriteria) throws ApplicationException {
        throw new UnsupportedOperationException();
        // String hql = hqlCriteria.getHqlString();
        //        
        // String upperHQL = hql.toUpperCase();
        // int index = upperHQL.indexOf(" FROM ");
        //        
        // hql = hql.substring(index + " FROM ".length()).trim()+" ";
        // String targetClassName = hql.substring(0,hql.indexOf(' ')).trim();
        // return privateQuery(hqlCriteria, targetClassName);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(gov.nih.nci.system.query.cql.CQLQuery,
     *      java.lang.String)
     */
    public List<Object> query(CQLQuery cqlQuery, String targetClassName) throws ApplicationException {
        throw new UnsupportedOperationException();
        // return query(cqlQuery);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(gov.nih.nci.system.query.cql.CQLQuery)
     */
    public List<Object> query(CQLQuery cqlQuery) throws ApplicationException {
        throw new UnsupportedOperationException();
        // return privateQuery(cqlQuery, cqlQuery.getTarget().getName());
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#search(java.lang.Class,
     *      java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public List<Object> search(Class targetClass, Object obj) throws ApplicationException {
        throw new UnsupportedOperationException();
        // return search(targetClass.getName(), obj);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#search(java.lang.Class,
     *      java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<Object> search(Class targetClass, List objList) throws ApplicationException {
        throw new UnsupportedOperationException();
        // return search(targetClass.getName(), objList);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#search(java.lang.String,
     *      java.lang.Object)
     */
    public List<Object> search(String path, Object obj) throws ApplicationException {
        throw new UnsupportedOperationException();
        // List<Object> list = new ArrayList<Object>();
        // list.add(obj);
        // return search(path, list);
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#query(java.lang.Object,
     *      java.lang.Integer, java.lang.String)
     */
    public List<Object> query(Object criteria, Integer firstRow, String targetClassName) throws ApplicationException {
        throw new UnsupportedOperationException();
        // Request request = new Request(criteria);
        //        
        // request.setIsCount(Boolean.valueOf(false));
        // request.setFirstRow(firstRow);
        // request.setDomainObjectName(targetClassName);
        //
        // Response response = query(request);
        // List<Object> results = (List<Object>) response.getResponse();
        //
        // return results;
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#search(java.lang.String,
     *      java.util.List)
     */
    @SuppressWarnings("unchecked")
    public List<Object> search(String path, List objList) throws ApplicationException {
        throw new UnsupportedOperationException();
        // try{
        // String targetClassName = "";
        // StringTokenizer tokens = new StringTokenizer(path, ",");
        // targetClassName = tokens.nextToken().trim();
        //            
        // NestedCriteriaPath crit = new NestedCriteriaPath(path,objList);
        // List<Object> results = privateQuery((Object)crit, targetClassName);
        //
        // return results;
        // }
        // catch(Exception e)
        // {
        // String msg = "Error while executing nested search criteria query";
        // log.error(msg,e);
        // throw new ApplicationException(msg,e);
        // }
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#getQueryRowCount(java.lang.Object,
     *      java.lang.String)
     */
    public Integer getQueryRowCount(Object criteria, String targetClassName) throws ApplicationException {
        throw new UnsupportedOperationException();
        // Integer count = null;
        // Response response = new Response();
        // Request request = new Request(criteria);
        // request.setIsCount(Boolean.TRUE);
        // request.setDomainObjectName(targetClassName);
        //
        // response = query(request);
        // count = response.getRowCount();
        //
        // if (count != null)
        // return count;
        // else
        // return 0;
    }

    /**
     * @see gov.nih.nci.system.applicationservice.ApplicationService#getAssociation(java.lang.Object,
     *      java.lang.String)
     */
    public List<Object> getAssociation(Object source, String associationName) throws ApplicationException {
        throw new UnsupportedOperationException();
        // String assocType = "";
        // try{
        // assocType =
        // classCache.getAssociationType(source.getClass(),associationName);
        // }catch(Exception e)
        // {
        // throw new ApplicationException(e);
        // }
        // String hql = "";
        // if(classCache.isCollection(source.getClass().getName(),
        // associationName))
        // //hql =
        // "select dest from "+assocType+" as dest,"+source.getClass().getName()+" as src where dest in elements(src."+associationName+") and src=?";
        // hql =
        // "select dest from "+source.getClass().getName()+" as src inner join src."+associationName+" dest where src=?";
        // else
        // hql =
        // "select dest from "+assocType+" as dest,"+source.getClass().getName()+" as src where src."+associationName+".id=dest.id and src=?";
        //
        // List<Object> params = new ArrayList<Object>();
        // params.add(source);
        // HQLCriteria criteria = new HQLCriteria(hql,params);
        // return query(criteria);
    }

    public Integer getMaxRecordsCount() {
        return maxRecordCount;
    }

    /**
     * Prepares the <tt>gov.nih.nci.system.dao.Request</tt> object and uses
     * {@link #query(Request)} to retrieve results from the data source. The
     * results are converted in the list which is only partially loaded up to
     * the maximum number of record that the system can support at a time.
     * 
     * @param criteria
     * @param targetClassName
     * @return a list of result objects.
     * @throws ApplicationException
     */
    protected List<Object> privateQuery(Object criteria, String targetClassName) throws ApplicationException {

        Request request = new Request(criteria);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);
        request.setDomainObjectName(targetClassName);

        Response response = query(request);
        List<Object> results = resultsFromResponse(response);

        List<Object> resultList = convertToListProxy(results, criteria, targetClassName, 0);
        myLogger.debug("response.getRowCount(): " + response.getRowCount());

        return resultList;
    }

    @SuppressWarnings("unchecked")
    private List<Object> resultsFromResponse(Response response) {
        return (List<Object>) response.getResponse();
    }

    protected List<Object> convertToListProxy(Collection<Object> originalList, Object query, String classname, Integer start) {
        ListProxy resultList = new ListProxy();
        resultList.setAppService(this);

        // Set the value for ListProxy
        if (originalList != null) {
            resultList.addAll(originalList);
        }

        resultList.setOriginalStart(start);
        resultList.setMaxRecordsPerQuery(getMaxRecordsCount());
        resultList.setOriginalCriteria(query);
        resultList.setTargetClassName(classname);
        resultList.calculateRealSize();

        return genericListToObjectList(resultList);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> genericListToObjectList(List l) {
        return l;
    }

    /**
     * Sends the request to the DAO. The DAO is determined by the object that
     * the query specifies to be queried. DAO returns the result which is in the
     * form of a <tt>gov.nih.nci.system.dao.Response</tt> object.
     * 
     * @param request
     *            The request that will drive the query.
     * @return A possibly partial set of results from the query.
     * @throws ApplicationException
     *             if there is a problem
     */
    protected Response query(Request request) throws ApplicationException {
        DAO dao = getDAO(request.getDomainObjectName());
        request.setClassCache(classCache);
        try {
            return dao.query(request);
        } catch (Exception exception) {
            myLogger.error("Error while querying DAO ", exception);
            throw new ApplicationException("Error while querying DAO: ", exception);
        }
    }

    protected DAO getDAO(String classname) throws ApplicationException {
        if (classname == null || classname.equals(""))
            throw new ApplicationException("No Domain Object name specified in the request; unable to locate corresponding DAO");

        DAO dao = classCache.getDAOForClass(classname);

        if (dao == null)
            throw new ApplicationException("Could not obtain DAO for: " + classname);

        return dao;
    }

    protected ClassCache getClassCache() {
        return classCache;
    }

    /**
     * Process a CQL query for a data service, subjecting the results to
     * constraints specified in CSM tables.
     */
    public List<Object> query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws ApplicationException {
        Session sess = null;
        try {
            sess = sessionFactory.openSession();
            CQL2DetachedCriteria converter = new CQL2DetachedCriteria(classCache, new RoleNameResolver(classCache), false);
            String userId = SecurityManager.getManager().getCaller();
            DetachedCriteria criteriaQuery = converter.convertToDetachedCriteria(cqlQuery, userId, sess);
            return query(criteriaQuery, sess);
        } catch (Exception e) {
            myLogger.error(e.getMessage(), e);
            throw new ApplicationException(e.getMessage(), e);
        } finally {
            if (sess != null) {
                sess.close();
            }
        }
    }

    private List<Object> query(DetachedCriteria detachedCriteria, Session sess) {
        CriteriaImpl crit = (CriteriaImpl) detachedCriteria.getExecutableCriteria(sess);
        String targetClassName = crit.getEntityOrClassName();

        Request request = new Request(detachedCriteria);
        request.setIsCount(Boolean.FALSE);
        request.setFirstRow(0);
        request.setDomainObjectName(targetClassName);

        List<Object> results;
        try {
            results = fetchQueryResults(crit);
        } catch (RuntimeException e) {
            String msg = "Error processing hibernate query\n" + " " + crit.toString();
            myLogger.error(msg);
            throw e;
        }

        List<Object> resultList = convertToListProxy(results, detachedCriteria, targetClassName, 0);

        return resultList;
    }

    @SuppressWarnings("unchecked")
    private List<Object> fetchQueryResults(CriteriaImpl crit) {
        return crit.list();
    }
}
