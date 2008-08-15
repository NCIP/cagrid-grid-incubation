package edu.umn.msi.cagrid.introduce.interfaces.types.mapping;

import javax.xml.namespace.QName;

import edu.umn.msi.cagrid.introduce.interfaces.types.TypeBean;
import edu.umn.msi.cagrid.introduce.interfaces.types.TypeBeanCollectionSupplier;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class TypeMappingImpl implements TypeMapping {
  private Map<String,QName> typeMap = new HashMap<String,QName>();
  
  public TypeMappingImpl(TypeBeanCollectionSupplier provider) {
    this(provider.get().iterator());
  }
  
  public TypeMappingImpl(Iterator<TypeBean> typeMappingBeans) {
    init(typeMappingBeans);
  }
  
  private void init(Iterator<TypeBean> typeMappingBeans) {
    while(typeMappingBeans.hasNext()) {
      TypeBean typeMappingBean = typeMappingBeans.next();
      String javaClassName = typeMappingBean.getJavaCanonicalName();
      if(typeMap.get(javaClassName) == null) {
        typeMap.put(javaClassName, typeMappingBean.getQName());
      } else {
        
      }
    }
  }
  
  public QName getQName(String canonicalName) {
    return typeMap.get(canonicalName);
  }

  public QName getQName(Class<? extends Object> class_) {
    return getQName(class_.getCanonicalName());
  }

}
