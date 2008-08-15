package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Predicate;

import edu.umn.msi.cagrid.introduce.interfaces.Constants;
import edu.umn.msi.cagrid.introduce.interfaces.types.mapping.TypeMapping;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputs;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeInputsInput;

public class MethodConfiguration {
  /**
   * The Java method that describes this method.
   */
  private Method method;
  
  /**
   * Name of the service operation to create for this method.
   */
  private String name;
  
  /**
   * Description of the service operation to create for this method.  
   */
  private String description;
  
  /**
   * Indicates whether to exclude this method from the web service.
   */
  private Boolean exclude = null;
  
  /**
   * Configuration object for the result of this operation.
   */
  private ResultConfiguration result;
  
  /**
   * Configuration objects for the parameters to this method.
   */
  private List<ParameterConfiguration> parameters = new LinkedList<ParameterConfiguration>();

  /**
   * Parent configuration object.
   */
  private InterfaceConfiguration interfaceConfiguration;
 
  /**
   * Service method name to delegate to (if applicable).
   */
  private String serviceMethodName;
  
  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getExclude() {
    return exclude;
  }

  public void setExclude(Boolean exclude) {
    this.exclude = exclude;
  }

  public ResultConfiguration getResult() {
    return result;
  }

  public void setResult(ResultConfiguration result) {
    this.result = result;
  }

  public List<ParameterConfiguration> getParameters() {
    return parameters;
  }

  public void setParameters(List<ParameterConfiguration> parameters) {
    this.parameters = parameters;
  }

  public InterfaceConfiguration getInterfaceConfiguration() {
    return interfaceConfiguration;
  }

  public void setInterfaceConfiguration(InterfaceConfiguration interfaceConfiguration) {
    this.interfaceConfiguration = interfaceConfiguration;
  }
  
  public String getServiceMethodName() {
    return serviceMethodName;
  }

  public void setServiceMethodName(String serviceMethodName) {
    this.serviceMethodName = serviceMethodName;
  }
  
  public void addDefaultValues(TypeMapping defaultTypeMapping) {
    if(exclude == null) {
      exclude = false;
    }
    if(name == null) {
      name = method.getName();
    }
    if(description == null) {
      description = Constants.DEFAULT_METHOD_DESCRIPTION;
    }
    result.addDefaultValues(defaultTypeMapping);
    int i = 0;
    for(ParameterConfiguration parameterConfiguration : parameters) {
      parameterConfiguration.addDefaultValues(defaultTypeMapping, i++);
    }
  }
  
  public static Predicate<MethodConfiguration> getIncludedPredicate() {
    return new Predicate<MethodConfiguration>() {
      public boolean apply(MethodConfiguration method) {
        return !method.getExclude() && (method.getServiceMethodName() == null || method.getServiceMethodName().equals(""));
      }      
    };
  }
  
  public MethodType getIntroduceMethodType() {
    MethodType methodType = new MethodType();
    methodType.setDescription(description);
    methodType.setName(name);
    
    int numParameters = method.getParameterTypes().length;
    MethodTypeInputsInput[] inputArray = new MethodTypeInputsInput[numParameters];
    for(int i = 0; i < numParameters; i++) {
      inputArray[i] = parameters.get(i).getIntroduceMethodTypeInputsInput();
    } 
    MethodTypeInputs inputs = new MethodTypeInputs();
    inputs.setInput(inputArray);
    methodType.setInputs(inputs);
    methodType.setOutput(result.getIntroduceMethodTypeOutput());
    methodType.setIsImported(false);
    methodType.setIsProvided(false);
    //methodType.setExceptions(getMethodTypeExceptions(typeMap, method));
    return methodType;
  }
    
  /**
   * Returns the unboxed method signature as it will appear in service impl class. 
   * Depends on introduce generating methods with the fully qualified class name for
   * each parameter and with no spaces between arguments (only a comma).
   * 
   * @param method Method to build signature of.
   * @return The resulting signature.
   */
  public String buildMethodSignature(boolean useJavaMethodName) {
    StringBuffer signature = new StringBuffer();
    signature.append("public " + getMethod().getReturnType().getCanonicalName() + " ");
    String methodName = useJavaMethodName ? method.getName() : name;
    signature.append(methodName + "("); 
    int i = 0;
    for(ParameterConfiguration parameterConfiguration : getParameters()) {
      if(i++ > 0) {
        signature.append(",");
      }
      String parameterName = parameterConfiguration.getName(); 
      signature.append(parameterConfiguration.getJavaType().getCanonicalName() + " " + parameterName);
    }
    return signature.append(")").toString();
  }
  
  public String buildMethodSignatureWithExceptions(boolean useJavaMethodName) {
    StringBuffer signature = new StringBuffer(buildMethodSignature(useJavaMethodName));
    boolean addedException = false;
    for(Class<?> exceptionClass : getMethod().getExceptionTypes()) {
      if(!addedException) {
        signature.append(" throws ");
        addedException = true;
      } else {
        signature.append(",");
      }
      signature.append(exceptionClass.getCanonicalName());
    }
    return signature.toString();
  }

  public String getDelegatedCallArguments() {
    StringBuffer args = new StringBuffer();
    int i = 0;
    for(ParameterConfiguration parameterConfiguration : getParameters()) {
      if(i++ > 0) {
        args.append(",");
      }
      String parameterName = parameterConfiguration.getName();
      args.append(parameterName);
    }
    return args.toString();
  }

  // Type casting the delegated calls lets Spring beans be declared as 
  // Objects!
  public String getDelegatedCall(String delegate, boolean cast) {
    StringBuffer call = new StringBuffer();
    String interfaceName = getInterfaceConfiguration().getName();
    if(cast) {
      call.append("((" + interfaceName + ")" + delegate + ")." + getMethod().getName() + "(");
    } else {
      call.append(delegate + "." + getName() + "(");
    }
    call.append(getDelegatedCallArguments());
    call.append(")");
    return call.toString();
  }

  public String getDelegatedCallStatement(String delegate, boolean cast) {
    StringBuffer statement = new StringBuffer();
    if(!getMethod().getReturnType().equals(Void.TYPE)) {
      statement.append("return ");
    }
    statement.append(getDelegatedCall(delegate,cast));
    statement.append(";");
    return statement.toString();
  }
}
