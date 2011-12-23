package edu.umn.msi.cagrid.introduce.interfaces.types;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.util.Collection;
import java.util.LinkedList;

public class PostCodegenTypeBeanCollectionSupplierImpl implements TypeBeanCollectionSupplier {
  private Collection<TypeBean> typeMappingBeans = new LinkedList<TypeBean>();
  
  public PostCodegenTypeBeanCollectionSupplierImpl(ServiceInformation information) {

    if ((information.getNamespaces() != null) && (information.getNamespaces().getNamespace() != null)) {
      for(NamespaceType namespace : information.getNamespaces().getNamespace()) {
        if(namespace.getSchemaElement() != null && !namespace.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
          // Skip the W3CNAMESPACE, these are added manually at the end of this method.
          for(SchemaElementType schemaElement : namespace.getSchemaElement()) {
            TypeBean bean = new TypeBean();
            bean.setNamespace(namespace.getNamespace());
            bean.setLocalPart(schemaElement.getType());
            bean.setJavaPackage(namespace.getPackageName());
            bean.setJavaClassName(schemaElement.getClassName());
            typeMappingBeans.add(bean);
          }
        }
      }
    }
    typeMappingBeans.addAll(new W3XmlSchemaTypeBeanCollectionSupplier().get());
  }
  
  public Collection<TypeBean> get() {
    return typeMappingBeans;
  }
}




