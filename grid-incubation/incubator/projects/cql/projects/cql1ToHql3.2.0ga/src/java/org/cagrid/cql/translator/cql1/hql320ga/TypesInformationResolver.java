package org.cagrid.cql.translator.cql1.hql320ga;

public interface TypesInformationResolver {

    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException;
    
    public boolean classHasSubclasses(String classname) throws TypesInformationException;
    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException;
    
    public String getRoleName(String parentClassname, String childClassname) throws TypesInformationException;
}
