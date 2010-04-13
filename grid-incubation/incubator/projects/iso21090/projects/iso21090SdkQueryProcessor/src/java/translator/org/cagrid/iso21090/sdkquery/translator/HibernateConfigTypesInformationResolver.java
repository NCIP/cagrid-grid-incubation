package org.cagrid.iso21090.sdkquery.translator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class HibernateConfigTypesInformationResolver implements TypesInformationResolver {
    
    private Configuration configuration = null;
    private Map<String, Boolean> subclasses = null;
    private Map<String, Object> discriminators = null;
    private Map<String, Class<?>> fieldDataTypes = null;
    private Map<String, String> roleNames = null;
    
    public HibernateConfigTypesInformationResolver(Configuration hibernateConfig) {
        this.configuration = hibernateConfig;
        this.subclasses = new HashMap<String, Boolean>();
        this.discriminators = new HashMap<String, Object>();
        this.fieldDataTypes = new HashMap<String, Class<?>>();
        this.roleNames = new HashMap<String, String>();
    }
    

    public boolean classHasSubclasses(String classname) throws TypesInformationException {
        Boolean hasSubclasses = subclasses.get(classname);
        if (hasSubclasses == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                hasSubclasses = Boolean.valueOf(clazz.hasSubclasses());
                subclasses.put(classname, hasSubclasses);
            } else {
                throw new TypesInformationException("Class " + classname + " not found in configuration");
            }
        }
        return hasSubclasses.booleanValue();
    }


    public Object getClassDiscriminatorValue(String classname) throws TypesInformationException {
        Object identifier = discriminators.get(classname);
        if (identifier == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                if (clazz instanceof Subclass) {
                    Subclass sub = (Subclass) clazz;
                    if (sub.isJoinedSubclass()) {
                        identifier = Integer.valueOf(sub.getSubclassId());
                    } else {
                        identifier = getShortClassName(classname);
                    }
                } else if (clazz instanceof RootClass) {
                    RootClass root = (RootClass) clazz;
                    if (root.getDiscriminator() == null) {
                        identifier = Integer.valueOf(root.getSubclassId());
                    } else {
                        identifier = getShortClassName(classname);
                    }
                }
            } else {
                throw new TypesInformationException("Class " + classname + " not found in hibernate configuration");
            }
            discriminators.put(classname, identifier);
        }
        return identifier;
    }

    
    public Class<?> getJavaDataType(String classname, String field) throws TypesInformationException {
        String fqName = classname + "." + field;
        Class<?> type = fieldDataTypes.get(fqName);
        if (type == null) {
            PersistentClass clazz = configuration.getClassMapping(classname);
            if (clazz != null) {
                // TODO: test that this barks up the inheritance tree for properties
                Property property = clazz.getRecursiveProperty(field);
                if (property != null) {
                    type = property.getType().getReturnedClass();
                } else {
                    throw new TypesInformationException("Field " + fqName + " not found in hibernate configuration");
                }
            } else {
                throw new TypesInformationException("Class " + classname + " not found in hibernate configuration");
            }
            fieldDataTypes.put(fqName, type);
        }
        return type;
    }


    public String getRoleName(String parentClassname, String childClassname) throws TypesInformationException {
        String identifier = getAssociationIdentifier(parentClassname, childClassname);
        String roleName = roleNames.get(identifier);
        if (roleName == null) {
            PersistentClass clazz = configuration.getClassMapping(parentClassname);
            Iterator<?> propertyIter = clazz.getPropertyIterator();
            while (propertyIter.hasNext()) {
                Property prop = (Property) propertyIter.next();
                Value value = prop.getValue();
                String referencedEntity = null;
                if (value instanceof Collection) {
                    Value element = ((Collection) value).getElement();
                    if (element instanceof OneToMany) {
                        referencedEntity = ((OneToMany) element).getReferencedEntityName();
                    } else if (element instanceof ToOne) {
                        referencedEntity = ((ToOne) element).getReferencedEntityName();
                    }
                } else if (value instanceof ToOne) {
                    referencedEntity = ((ToOne) value).getReferencedEntityName();
                }
                if (childClassname.equals(referencedEntity)) {
                    if (roleName != null) {
                        // already found one association, so this is ambiguous
                        throw new TypesInformationException("Association from " + parentClassname + " to " 
                            + childClassname + " is ambiguous.  Please specify a valid role name");
                    }
                    roleName = prop.getName();
                }
            }
        }
        return roleName;
    }
    
    
    private String getShortClassName(String className) {
        int dotIndex = className.lastIndexOf('.');
        return className.substring(dotIndex + 1);
    }
    
    
    private String getAssociationIdentifier(String parentClassname, String childClassname) {
        return parentClassname + "-->" + childClassname;
    }
}
