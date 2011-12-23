package edu.umn.msi.cagrid.introduce.interfaces.client;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Loosely based on javax.jws.WebMethod as specified by JSR 181.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface GridMethod {
  boolean exclude() default false;
  String operationName() default "";
  String description() default "";
  String delegateToServiceMethod() default "";
}
