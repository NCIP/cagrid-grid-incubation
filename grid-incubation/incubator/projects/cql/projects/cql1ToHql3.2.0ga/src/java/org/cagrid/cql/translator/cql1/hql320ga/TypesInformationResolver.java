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
package org.cagrid.cql.translator.cql1.hql320ga;

/**
 * TypesInformationResolver
 * Used to resolve various information about domain datatypes
 * 
 * @author David
 */
public interface TypesInformationResolver {

    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException;
    
    public boolean classHasSubclasses(String classname) throws TypesInformationException;
    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException;
    
    public String getRoleName(String parentClassname, String childClassname) throws TypesInformationException;
}
