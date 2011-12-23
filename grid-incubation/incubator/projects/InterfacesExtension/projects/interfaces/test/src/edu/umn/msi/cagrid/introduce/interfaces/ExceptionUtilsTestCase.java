package edu.umn.msi.cagrid.introduce.interfaces;

import junit.framework.TestCase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.AccessException;

import edu.umn.msi.cagrid.introduce.interfaces.RemoteExceptionUtils;

public class ExceptionUtilsTestCase extends TestCase {
  class NewException extends Exception {
    private static final long serialVersionUID = 1L;
  }
  
  class NewIOException extends IOException {
    private static final long serialVersionUID = 1L;
  }
  
  class NewRemoteException extends RemoteException {
    private static final long serialVersionUID = 1L;
  }
  
  class Foo {
    public void foo1() {}
    
    public void foo2() throws IOException {}
    
    public void foo3() throws AccessException {}
    
    public void foo4() throws RemoteException {}
    
    public void foo5() throws RemoteException, IOException {}
    
    public int foo6() throws RemoteException { return 1; }
    
    public int foo7() throws AccessException, IOException { return 0; }
 
    public void foo8() throws IOException, FileNotFoundException { };
  }
  
  public void testRequiresExceptionWrapping() throws Exception {
    Class<Foo> fooClass = Foo.class;
    assertTrue(!RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo1"), null));
    assertTrue(RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo2"), null));
    assertTrue(!RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo3"), null));
    assertTrue(!RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo4"), null));
    assertTrue(RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo5"), null));
    assertTrue(!RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo6"), null));
    assertTrue(RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo7"), null));
    assertTrue(RemoteExceptionUtils.serviceMethodRequiresExceptionWrapper(fooClass.getMethod("foo8"), null));
  }
  
  public void testThrowsRemoteException() throws Exception {
    Class<Foo> fooClass = Foo.class;
    assertTrue(!RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo1")));
    assertTrue(!RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo2")));
    assertTrue(RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo3")));
    assertTrue(RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo4")));
    assertTrue(RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo5")));
    assertTrue(RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo6")));
    assertTrue(RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo7")));
    assertTrue(!RemoteExceptionUtils.throwsRemoteException(fooClass.getMethod("foo8")));
  }
  
}
