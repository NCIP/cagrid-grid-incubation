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
package edu.umn.msi.cagrid.introduce.interfaces.codegen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Unit test
public class StringBufferUtils {

  public static void replaceFirst(StringBuffer buffer, StringBuffer regex, CharSequence replacement) {
    replaceFirst(buffer, regex.toString(), replacement);
  }
  
  public static void replaceFirst(StringBuffer buffer, String regex, CharSequence replacement) {
    replaceFirst(buffer, Pattern.compile(regex), replacement);
  }
  
  public static void replaceFirst(StringBuffer buffer, Pattern pattern, CharSequence replacement) {
    Matcher matcher = pattern.matcher(buffer);
    if(matcher.find()) {
      int start = matcher.start();
      buffer.delete(start, start+matcher.group().length());
      buffer.insert(start, replacement);
    }
  }
  
  public static void replaceAll(StringBuffer buffer, StringBuffer regex, CharSequence replacement) {
    replaceAll(buffer, regex.toString(), replacement);
  }
  
  public static void replaceAll(StringBuffer buffer, String regex, CharSequence replacement) {
    replaceAll(buffer, Pattern.compile(regex), replacement);
  }
  
  public static void replaceAll(StringBuffer buffer, Pattern pattern, CharSequence replacement) {
    Matcher matcher;
    int location = 0;
    while((matcher = pattern.matcher(buffer)).find(location)) {
      location = matcher.start();

      buffer.delete(location, location+matcher.group().length());
      buffer.insert(location, replacement);
      location += replacement.length();
    }
  }
  
  public static int countOccurrences(StringBuffer buffer, String query) {
    int count = 0;
    int location = -1;
    while((location = buffer.indexOf(query, location+1)) != -1) {
      count++;
    }
    return count;
  }
  
  public static int countMatches(StringBuffer buffer, String regex) {
    return countMatches(buffer, Pattern.compile(regex));
  }

  private static int countMatches(StringBuffer buffer, Pattern pattern) {
    Matcher matcher = pattern.matcher(buffer);
    int count = 0;
    int location = 0;
    while(matcher.find(location)) {
      location = matcher.start()+1;
      count++;
    }
    return count;
  }
  
  
  
}
