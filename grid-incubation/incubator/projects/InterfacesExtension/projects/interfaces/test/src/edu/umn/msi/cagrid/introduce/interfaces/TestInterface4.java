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
