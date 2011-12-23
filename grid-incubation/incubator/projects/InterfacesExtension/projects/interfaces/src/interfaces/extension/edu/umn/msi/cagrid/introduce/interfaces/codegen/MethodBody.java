package edu.umn.msi.cagrid.introduce.interfaces.codegen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nih.nci.cagrid.introduce.codegen.services.methods.SyncHelper;

public class MethodBody {
  private String signature;
  private StringBuffer source;

  protected class MethodBoundary {
    private int start; // First character after newline following open bracket, or first nonwhitespace after open bracket
    private int end; // First character on line containing close bracket or first nonwhitespace on line of close bracket

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public MethodBoundary() {
//      int methodStart = SyncHelper.startOfSignature(source, signature);
      int methodStart = source.indexOf(signature);
      int firstBracket = source.indexOf("{",methodStart);
      start = firstBracket+1;
      while(true) {
        char curChar = source.charAt(start);
        if(!Character.isWhitespace(curChar)) {
          break;
        } else if(curChar == '\n' || curChar == '\r') {
          start++;
          break;
        }
        start++;
      } 
      end = SyncHelper.bracketMatch(source, firstBracket); 
      while(true) {
        char curChar = source.charAt(end);
        if(curChar == '}') {
          break;
        }
        end--;
      }
      while(true) {
        end--;
        char curChar = source.charAt(end);
        if(curChar == '\n' || curChar == '\r' || !Character.isWhitespace(curChar)) {
          end++;
          break;
        }
      }
    }
  }

  public MethodBody(StringBuffer source, String signature) {
    this.signature = signature;
    this.source = source;
  }

  public void setContents(String body) {
    MethodBoundary boundary = new MethodBoundary();
    source.delete(boundary.getStart(), boundary.getEnd());
    source.insert(boundary.getStart(), body);
  }

  public String guessIndentation() {
    MethodBoundary boundary = new MethodBoundary();
    int endIndex = boundary.getEnd();
    int bracketIndex = source.indexOf("}", endIndex);
    CharSequence endToBracket = source.substring(endIndex, bracketIndex);
    return endToBracket.toString();
  }

  public String indent(String code) {
    String indentation = guessIndentation();
    char lastChar = code.charAt(code.length()-1);
    code = code.replaceAll("\n", "\n" + indentation + indentation);
    code = code.replaceAll("\r", "\r" + indentation + indentation);
    if(lastChar =='\n' || lastChar == '\r') {
      code = code.substring(0, code.length() - indentation.length()*2);
    }
    return indentation + indentation + code;
  }

  public void append(String code) {
    append(code, false);
  }

  public void prepend(String code) {
    prepend(code, false);
  }

  public void append(String code, boolean indent) {
    MethodBoundary boundary = new MethodBoundary();
    if(indent) {
      code = indent(code);
    }
    source.insert(boundary.getEnd(), code);
  }

  public void prepend(String code, boolean indent) {
    MethodBoundary boundary = new MethodBoundary();
    if(indent) {
      code = indent(code);
    }
    source.insert(boundary.getStart(), code);
  }

  public CharSequence getContents() {
    MethodBoundary boundary = new MethodBoundary();
    return source.subSequence(boundary.getStart(), boundary.getEnd());
  }

  public String toString() {
    return getContents().toString();
  }

  /* Pattern matching against the method body. */
  public boolean contains(String regex) {
    return contains(Pattern.compile(regex));
  }

  public boolean contains(Pattern pattern) {
    Matcher matcher = getMatcher(pattern);
    return matcher.find();
  }

  public Matcher getMatcher(Pattern pattern) {
    Matcher matcher = pattern.matcher(getContents());
    return matcher;
  }

  public Matcher getMatcher(String regex) {
    return getMatcher(Pattern.compile(regex));
  }
}


