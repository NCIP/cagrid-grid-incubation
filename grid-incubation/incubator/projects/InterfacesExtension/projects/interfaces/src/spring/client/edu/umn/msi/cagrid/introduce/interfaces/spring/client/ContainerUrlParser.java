/**
*============================================================================
*  The Ohio State University Research Foundation, Emory University,
*  the University of Minnesota Supercomputing Institute
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-grid-incubation/LICENSE.txt for details.
*============================================================================
**/
/**
*============================================================================
*============================================================================
**/
package edu.umn.msi.cagrid.introduce.interfaces.spring.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ContainerUrlParser {
  private static final Pattern URL_PATTERN = Pattern.compile("(\\w+)://(.+):(\\d+)/?");
  
  private static Matcher matchUrl(String url) {
    final Matcher matcher = URL_PATTERN.matcher(url);
    if(!matcher.matches()) {
      throw new IllegalStateException("Invalid container url encountered " + url);
    }
    return matcher;
  }
  static int getPort(String url) {
    return Integer.parseInt(matchUrl(url).group(3));
  }
  
  static String getScheme(String url) {
    return matchUrl(url).group(1);
  }
  
  static String getHostname(String url) {
    return matchUrl(url).group(2);
  }
  
}
