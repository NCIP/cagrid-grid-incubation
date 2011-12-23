package edu.umn.msi.cagrid.introduce.interfaces.configuration;


import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;

import edu.umn.msi.cagrid.introduce.interfaces.client.GridMethod;
import edu.umn.msi.cagrid.introduce.interfaces.client.GridParam;
import edu.umn.msi.cagrid.introduce.interfaces.client.GridResult;
import edu.umn.msi.cagrid.introduce.interfaces.client.service.ImplementsForService;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.DocletUtils;
import edu.umn.msi.cagrid.introduce.interfaces.codegen.Regexes;
import edu.umn.msi.cagrid.introduce.interfaces.services.Service;
 
public class ConfigurationFactory {
  public ServiceConfiguration getServiceConfiguration(Service service) throws IOException {
    String source = service.getServiceImplContents();
    return getServiceConfiguration(source, service.getCaGridService().getClassLoader());
  }
  
  private LinkedList<String> getInterfaces(AbstractJavaEntity entity) {
    LinkedList<String> interfaces = new LinkedList<String>();
    for(Annotation annotation : entity.getAnnotations()) {
      if(annotation.getType().getValue().equals(ImplementsForService.class.getCanonicalName())) {
        // Annotations of the form {"a", "b"} are lists of lists of strings for some reason?
        Object value = annotation.getNamedParameter("interfaces");
        if(value instanceof Collection) {
          for(Object element : (Collection<?>) value) {
            if(element instanceof Collection) { 
              for(Object subElement : (Collection<?>) element) {
                interfaces.add(fixQdoxAnnotationString(subElement.toString()));
              }
            } else {
              interfaces.add(fixQdoxAnnotationString(element.toString()));
            }
          }
        } else {
          interfaces.add(fixQdoxAnnotationString(value.toString()));
        }
      }
    } // End loop over annotations, interfaces is set
    return interfaces;
  }
  
  public ServiceConfiguration getServiceConfiguration(String source, ClassLoader loader) {
    DocletUtils.HasAnnotation predicate = new DocletUtils.HasAnnotation(ImplementsForService.class);
    LinkedList<FieldConfiguration> fieldConfigurations = new LinkedList<FieldConfiguration>();
    for(JavaField field : DocletUtils.findFields(source, predicate)) {
      LinkedList<String> interfaces = getInterfaces(field); 
      fieldConfigurations.add(getFieldConfiguration(getInterfaceClasses(interfaces, loader), field.getName(), false));
    } // End loop over fields
    for(JavaMethod method : DocletUtils.findMethods(source, predicate)) {
      LinkedList<String> interfaces = getInterfaces(method);
      fieldConfigurations.add(getFieldConfiguration(getInterfaceClasses(interfaces, loader), method.getName(), true));
    }
    ServiceConfiguration serviceConfiguration = new ServiceConfiguration();
    serviceConfiguration.setFields(fieldConfigurations);
    return serviceConfiguration;
  }
  
  /**
   * The qdox parser seems to choke on many simple annotations. The following hack attempts 
   * to fix strings the qdox parser produces. This hack removes comments, extra whitespace 
   * brackets and quotation marks that might be in the resulting string though they are not
   * supposed to be.
   */
  public String fixQdoxAnnotationString(String value) {
    Pattern singleLineCommentPattern = Pattern.compile("\\/\\/[^\n\r]*[\n\r]", Pattern.MULTILINE);
    value = singleLineCommentPattern.matcher(value).replaceAll("");
    Pattern multiLineCommentPattern2 = Pattern.compile(Regexes.JAVA_COMMENT_MULTILINE);
    value = multiLineCommentPattern2.matcher(value).replaceAll("");
    return value.replaceAll("[\\s\\[\"]", "");
  }
  
  
  public FieldConfiguration getFieldConfiguration(Collection<Class<?>> interfaces, String fieldName, boolean isMethod) {
    FieldConfiguration fieldConfiguration = new FieldConfiguration();
    fieldConfiguration.setName(fieldName);
    fieldConfiguration.setMethod(isMethod);
    for(Class<?> interface_ : interfaces) {
      InterfaceConfiguration interfaceConfiguration = getInterfaceConfiguration(interface_);
      fieldConfiguration.getInterfaces().add(interfaceConfiguration);
      interfaceConfiguration.setFieldConfiguration(fieldConfiguration);
    }
    
    return fieldConfiguration;
  }
  
  
  public InterfaceConfiguration getInterfaceConfiguration(Class<?> interface_) {
    InterfaceConfiguration interfaceConfiguration = new InterfaceConfiguration();
    interfaceConfiguration.setName(interface_.getCanonicalName());
    for(Method method : interface_.getMethods()) {
      MethodConfiguration methodConfiguration = getMethodConfiguration(interface_, method); 
      interfaceConfiguration.getMethods().add(methodConfiguration);
      methodConfiguration.setInterfaceConfiguration(interfaceConfiguration);
    }
    return interfaceConfiguration;
  }
  
  
  
  
  /**
   * Get the underlying class for a type, or null if the type is a variable type.
   * 
   * This code was found here...
   * 
   * http://www.artima.com/weblogs/viewpost.jsp?thread=208860
   * 
   * @param type the type
   * @return the underlying class
   */
  private static Class<?> getClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    else if (type instanceof ParameterizedType) {
      return getClass(((ParameterizedType) type).getRawType());
    }
    else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      Class<?> componentClass = getClass(componentType);
      if (componentClass != null ) {
        return Array.newInstance(componentClass, 0).getClass();
      }
      else {
        return null;
      }
    }
    else {
      return null;
    }
  } 
  
  /**
   * Get the actual type arguments a child class has used to extend a generic base class.
   *
   * @return a list of the raw classes for the actual type arguments.
   */
  public static Class<?> getClass(Class<?> class_, Type typeQuery) {
    Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
    LinkedList<Type> typesHeap = new LinkedList<Type>();
    typesHeap.add(class_);
    while(!typesHeap.isEmpty()) {
      Type type = typesHeap.removeFirst();
      if(type == null || type.equals(Object.class)) {
        continue;
      }
      if (type instanceof Class) {
        // there is no useful information for us in raw types, so just keep going.
        typesHeap.addAll(Arrays.asList(((Class<?>) type).getGenericInterfaces()));
      }
      else {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        if (rawType != null && !rawType.equals(Object.class)) {
          typesHeap.addAll(Arrays.asList(rawType.getGenericInterfaces()));
        }  
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
        for (int i = 0; i < actualTypeArguments.length; i++) {
          resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
        }
      } 
    }
    
    while(resolvedTypes.containsKey(typeQuery)) {
      typeQuery = resolvedTypes.get(typeQuery);
    }

    return getClass(typeQuery);
  } 
  
  public MethodConfiguration getMethodConfiguration(Class<?> interface_, Method method) {
    MethodConfiguration methodConfiguration = new MethodConfiguration();
    methodConfiguration.setMethod(method);
    methodConfiguration.setDescription((String) AnnotationUtils.getAnnotationValue(GridMethod.class, method, "description"));
    methodConfiguration.setName((String) AnnotationUtils.getAnnotationValue(GridMethod.class, method, "operationName"));
    methodConfiguration.setExclude((Boolean) AnnotationUtils.getAnnotationValue(GridMethod.class, method, "exclude"));
    methodConfiguration.setServiceMethodName((String) AnnotationUtils.getAnnotationValue(GridMethod.class, method, "delegateToServiceMethod"));
    
    ResultConfiguration resultConfiguration = getResultConfiguration(interface_, method);
    methodConfiguration.setResult(resultConfiguration);
    resultConfiguration.setMethodConfiguration(methodConfiguration);
    
    Type[] parameterTypes = method.getGenericParameterTypes();
    for(int i = 0; i < parameterTypes.length; i++) {
      if(parameterTypes[i] == null) {
        throw new NullPointerException("parameter type of argument" + (i+1) + " is null");
      }
      ParameterConfiguration parameterConfiguration = getParameterConfiguration(getClass(interface_, parameterTypes[i]), method.getParameterAnnotations()[i]);
      methodConfiguration.getParameters().add(parameterConfiguration);
      parameterConfiguration.setMethodConfiguration(methodConfiguration);
    }
    
    return methodConfiguration;
  }
  
  public ParameterConfiguration getParameterConfiguration(Class<?> class_, java.lang.annotation.Annotation[] annotations) {
    ParameterConfiguration parameterConfiguration = new ParameterConfiguration();
    parameterConfiguration.setJavaType(class_);
    parameterConfiguration.setName((String) AnnotationUtils.getAnnotationValue(GridParam.class, annotations, "name"));
    parameterConfiguration.setDescription((String) AnnotationUtils.getAnnotationValue(GridParam.class, annotations, "description"));
    String namespaceURI = (String) AnnotationUtils.getAnnotationValue(GridParam.class, annotations, "namespaceURI");
    String localPart = (String) AnnotationUtils.getAnnotationValue(GridParam.class, annotations, "localPart");
    parameterConfiguration.setXmlType(getQName(namespaceURI, localPart));
    return parameterConfiguration;  
  }

  public ResultConfiguration getResultConfiguration(Class<?> interface_, Method method) {
    ResultConfiguration resultConfiguration = new ResultConfiguration();
    resultConfiguration.setDescription((String) AnnotationUtils.getAnnotationValue(GridResult.class, method, "description"));
    String namespaceURI = (String) AnnotationUtils.getAnnotationValue(GridResult.class, method, "namespaceURI");
    String localPart = (String) AnnotationUtils.getAnnotationValue(GridResult.class, method, "localPart");
    resultConfiguration.setXmlType(getQName(namespaceURI, localPart));
    resultConfiguration.setJavaType(getClass(interface_, method.getGenericReturnType()));
    return resultConfiguration;
  }
  
  protected static QName getQName(String namespaceURI, String localPart) {
    QName qName = null;
    if(namespaceURI != null && !namespaceURI.equals("") &&
        localPart != null && !localPart.equals("")) {
      qName = new QName(namespaceURI, localPart);
    }
    return qName;   
  }

  protected static Collection<Class<?>> getInterfaceClasses(Collection<String> interfaces, ClassLoader classLoader) {
    HashSet<Class<?>> classes = new HashSet<Class<?>>();
    for(String interface_ : interfaces) {
      Class<?> class_; 
      try {
        if(classLoader == null) {
          class_ = Class.forName(interface_);
        } else {
          class_ = classLoader.loadClass(interface_);
        }
      } catch(ClassNotFoundException e) {
        throw new IllegalArgumentException("Interface " + interface_ + " not found.",e); 
      }
      if(!class_.isInterface()) {
        throw new IllegalArgumentException("Mapped class " + interface_ + " is not an interface.");
      }
      classes.add(class_);
    }
    return classes;
  }  
}
