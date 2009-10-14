package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import junit.framework.TestCase;

public class ContainerUrlParserTestCase extends TestCase {
  
  
  public void testParse() {
    final String url1 = "http://123.125.0.255:92/";
    assertEquals(ContainerUrlParser.getPort(url1), 92);
    assertEquals(ContainerUrlParser.getHostname(url1), "123.125.0.255");
    assertEquals(ContainerUrlParser.getScheme(url1), "http");

    final String url2 = "http://123.125.0.255:92";
    assertEquals(ContainerUrlParser.getPort(url2), 92);
    assertEquals(ContainerUrlParser.getHostname(url2), "123.125.0.255");
    assertEquals(ContainerUrlParser.getScheme(url2), "http");

    final String url3 = "https://moo.com:192/";
    assertEquals(ContainerUrlParser.getPort(url3), 192);
    assertEquals(ContainerUrlParser.getHostname(url3), "moo.com");
    assertEquals(ContainerUrlParser.getScheme(url3), "https");
  
  }
}
