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
package org.cagrid.cql2.preview.processor;

import gov.nih.nci.cagrid.metadata.common.UMLClass;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLAssociation;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClassReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/** 
 *  RoleNameResolver
 *  Utility for determining role names of associations
 * 
 * @author David Ervin
 * 
 * @created Dec 20, 2007 11:40:22 AM
 * @version $Id: RoleNameResolver.java,v 1.1 2008/04/02 14:46:19 dervin Exp $ 
 */
public class RoleNameResolver {
    private static Logger LOG = Logger.getLogger(RoleNameResolver.class);
    
    private DomainModel domainModel = null;
    private Map<String, List<ClassAssociation>> roleNames = null;
    
    public RoleNameResolver(DomainModel domainModel) {
        this.domainModel = domainModel;
        this.roleNames = new HashMap<String, List<ClassAssociation>>();
    }
    

    /**
     * Gets the role name of an association from the perspective of the parent
     * 
     * @param parentClassName
     *      The name of the parent class of the association
     * @return
     *      The determined role name
     * @throws Exception
     */
    public List<ClassAssociation> getAssociationRoleNames(String parentClassName) throws Exception {
        List<ClassAssociation> names = roleNames.get(parentClassName);
        if (names == null) {
            LOG.debug("Role names for " + parentClassName + " not found in cache... locating in model");

            UMLClassReference sourceRef = DomainModelUtils.getClassReference(domainModel, parentClassName);
            
            // get associations from the source to the target
            List<UMLAssociation> associations = getUmlAssociations(sourceRef);
            names = new ArrayList<ClassAssociation>(associations.size());
            for (UMLAssociation assoc : associations) {
                UMLClass associatedClass = null;
                String roleName = null;
                if (assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge()
                        .getUMLClassReference().getRefid().equals(sourceRef.getRefid())) {
                    // source edge matches
                    UMLClassReference ref = assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge().getUMLClassReference();
                    associatedClass = DomainModelUtils.getReferencedUMLClass(domainModel, ref);
                    roleName = assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge().getRoleName();
                } else if (assoc.isBidirectional() &&
                    assoc.getTargetUMLAssociationEdge().getUMLAssociationEdge()
                        .getUMLClassReference().getRefid().equals(sourceRef.getRefid())) {
                    UMLClassReference ref = assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge().getUMLClassReference();
                    associatedClass = DomainModelUtils.getReferencedUMLClass(domainModel, ref);
                    roleName = assoc.getSourceUMLAssociationEdge().getUMLAssociationEdge().getRoleName();
                }
                if (associatedClass != null && roleName != null) {
                    String fullClassName = associatedClass.getClassName();
                    if (associatedClass.getPackageName() != null && associatedClass.getPackageName().length() != 0) {
                        fullClassName = associatedClass.getPackageName() + "." + fullClassName;
                    }
                    names.add(new ClassAssociation(fullClassName, roleName));
                }
            }
            
            roleNames.put(parentClassName, names);
        }
        
        return names;
    }
    
    
    private List<UMLAssociation> getUmlAssociations(UMLClassReference sourceRef) {
        List<UMLAssociation> associations = new LinkedList<UMLAssociation>();
        if (domainModel.getExposedUMLAssociationCollection() != null &&
            domainModel.getExposedUMLAssociationCollection().getUMLAssociation() != null) {
            for (UMLAssociation assoc : domainModel.getExposedUMLAssociationCollection().getUMLAssociation()) {
                UMLClassReference sourceReference = assoc.getSourceUMLAssociationEdge()
                    .getUMLAssociationEdge().getUMLClassReference();
                UMLClassReference targetReference = assoc.getTargetUMLAssociationEdge()
                    .getUMLAssociationEdge().getUMLClassReference();
                // if source in association matches source class, add it.
                // also, if the association is bidirectional, the target can match too
                if (sourceReference.getRefid().equals(sourceRef.getRefid()) ||
                    (assoc.isBidirectional() && targetReference.getRefid().equals(sourceRef.getRefid()))) {
                    associations.add(assoc);
                }
            }
        }
        return associations;
    }
    
    
    public static class ClassAssociation {
        private String className;
        private String roleName;
        
        public ClassAssociation(String className, String roleName) {
            this.className = className;
            this.roleName = roleName;
        }
        
        
        public String getClassName() {
            return className;
        }
        
        
        public String getRoleName() {
            return roleName;
        }
    }
}
