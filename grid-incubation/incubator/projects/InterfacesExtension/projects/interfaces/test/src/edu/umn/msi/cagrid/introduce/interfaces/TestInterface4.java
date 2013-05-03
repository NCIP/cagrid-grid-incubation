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
package edu.umn.msi.cagrid.introduce.interfaces;

interface WayInnerTest<T> {
  T get(T arg);
}

interface InnerTest<S,T> extends WayInnerTest<T> {
  S foo(S arg1, String arg2);
}

interface Specified1<S> extends InnerTest<Integer,S> {
  
}

interface Specified2 extends Specified1<String> {
  
}

public interface TestInterface4 extends Specified2 {

}
