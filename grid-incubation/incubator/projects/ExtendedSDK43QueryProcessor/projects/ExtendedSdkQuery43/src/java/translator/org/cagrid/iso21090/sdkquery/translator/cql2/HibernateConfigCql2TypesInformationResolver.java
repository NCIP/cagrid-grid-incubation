package org.cagrid.iso21090.sdkquery.translator.cql2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cagrid.iso21090.sdkquery.translator.HibernateConfigTypesInformationResolver;
import org.cagrid.iso21090.sdkquery.translator.TypesInformationException;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;

public class HibernateConfigCql2TypesInformationResolver extends HibernateConfigTypesInformationResolver implements Cql2TypesInformationResolver {
    
    private Configuration configuration = null;
    private Map<String, List<ClassAssociation>> classAssociations = null;

    public HibernateConfigCql2TypesInformationResolver(Configuration hibernateConfig, boolean reflectionFallback) {
        super(hibernateConfig, reflectionFallback);
        this.configuration = hibernateConfig;
        this.classAssociations = new HashMap<String, List<ClassAssociation>>();
    }

    
    public List<ClassAssociation> getAssociationsFromClass(String parentClassname) throws TypesInformationException {
        List<ClassAssociation> associations = classAssociations.get(parentClassname);
        if (associations == null) {
            associations = new ArrayList<ClassAssociation>();
            PersistentClass clazz = configuration.getClassMapping(parentClassname);
            Iterator<?> propertyIter = clazz.getPropertyIterator();
            while (propertyIter.hasNext()) {
                Property property = (Property) propertyIter.next();
                Value value = property.getValue();
                if (value instanceof ToOne || value instanceof OneToMany) {
                    ClassAssociation assoc = new ClassAssociation(property.getType().getName(), property.getName());
                    associations.add(assoc);
                }
            }
            classAssociations.put(parentClassname, associations);
        }
        return associations;
    }
}
