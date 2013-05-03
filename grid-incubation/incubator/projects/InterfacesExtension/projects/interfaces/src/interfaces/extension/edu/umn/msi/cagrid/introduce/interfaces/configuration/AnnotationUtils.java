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
package edu.umn.msi.cagrid.introduce.interfaces.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtils {

  public static Object getAnnotationValue(Class<? extends Annotation> annotationType, AnnotatedElement element) {
    return AnnotationUtils.getAnnotationValue(annotationType, element, "value");
  }

  public static Object getAnnotationValue(Class<? extends Annotation> annotationType, AnnotatedElement element, String field) {
    return AnnotationUtils.getAnnotationValue(annotationType, element.getAnnotations(), field);
  }

  public static Object getAnnotationValue(Class<? extends Annotation> annotationType, Annotation[] annotations) {
    return AnnotationUtils.getAnnotationValue(annotationType, annotations, "value");
  }

  /**
   * Grabs the specified field for the given annotation type from the array 
   * of annotations present.
   */
  /* 
   * Benign looking hack! The deal is these annotations come from fields, 
   * methods, and classes that were loaded by a different Java class loader, 
   * so I can't directly compare annotationType to the type of the given 
   * annotations because while they correspond to the same concept of a 
   * class, they were loaded by different class loaders so equals fails on 
   * them. Hence I am comparing the fully qualified class names. Likewise
   * I cannot type cast them and call the field providing methods, like value(), 
   * so I am grabbing them via reflection.
   */
  public static Object getAnnotationValue(Class<? extends Annotation> annotationType, Annotation[] annotations, String field) {
    for(Annotation annotation : annotations) {
      if(annotation.annotationType().getCanonicalName().equals(annotationType.getCanonicalName())) {
        try {
          return annotation.getClass().getMethod(field).invoke(annotation);
        } catch (Exception e) {
          throw new IllegalStateException("Jars containing annotation declarations between the extension and the caGrid service seem to be different. Please resolve this.",e);
        }
      }
    }
    return null;
  }

}
