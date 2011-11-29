/**
 * Copyright 2010 Emory University
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated  documentation files (the 
 * "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions: The above copyright notice and this permission
 * notice shall be included in all copies or substantial portions of the
 * Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package edu.emory.cci.cqlCsm;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Instances of this class are used to describe CSM filters that may be
 * associated with a class. The information in a filter can be used to add
 * restriction to a query so as to limit what it returns to only values that a
 * user is authorized to see.
 * <p>
 * A filter contains the name of the class to which it applies. There is an
 * attribute name. The existence of the filter implies that instances of the
 * class to which the filter applies may only be accessed if the user has a
 * non-empty list of values associated with the attribute and the object to
 * which the attribute is associated has one of the value in the list.
 * <P>
 * A filter also has a possibly empty list of associations. This list of
 * associations describes the relationship between the class and the attribute.
 * If the list is empty then the attribute is associated the the same class as
 * the filter. Otherwise, the attribute is associated with the class at the
 * other end of the association path.
 * <p>
 * The elements of the association path are role names attached to associations.
 * The role names are presumed to uniquely identify the associations.
 * <p>
 * Filters also have associated with them a target class. The name of the target
 * class should match the name of the class at the other end of the association
 * path. This is used as a sanity check by comparing this name with the
 * service's domain model.
 * 
 * @author Mark Grand
 */
public class Filter {
    private static final Pattern selfPattern = Pattern.compile(" - self$");
    private static final Pattern classPattern = Pattern.compile("^[A-Za-z0-9_$]+ - ");
    
    private String className;
    private String[] associationPath;
    private String associationPathString;
    private String attributeName;
    private String targetClassName;

    /**
     * Constructor
     * 
     * @param className
     *            The name of the class that this filter is associated with.
     * @param associationPathString
     *            The is a representation of the association path as a single
     *            string. Non-empty association paths will be represented as one
     *            or more role names separated by a comma and zero or more
     *            spaces. Role names are assumed not to contain dots '.'.
     *            <p>
     *            A string that begins with the class name (contains a '.') is
     *            assumed to represent the empty association path.
     * @param attributeName
     *            The name of the attribute that is associated with this filter.
     * @param targetClassName
     *            The name of the class that is expected at the end of the
     *            association path. If this contains any blanks characters from
     *            the first blank onward are stripped off.
     */
    public Filter(String className, String associationPathString, String attributeName, String targetClassName) {
        super();
        this.className = className.trim();
        this.associationPathString = associationPathString.trim();
        if (this.associationPathString.length()==0 || this.associationPathString.startsWith(className)) {
            this.associationPath = new String[] {};
        } else {
            this.associationPath = associationPathString.split("[, ]+");
            for (int i = 0; i < this.associationPath.length; i++) {
                int index = associationPath[i].indexOf(" - ");
                if (index > 0) {
                    associationPathString.substring(index+3);
                }
            }
        }
        this.attributeName = attributeName.trim();
        this.targetClassName = targetClassName.trim();
        this.targetClassName = selfPattern.matcher(this.targetClassName).replaceFirst("");
        this.targetClassName = classPattern.matcher(this.targetClassName).replaceFirst("");
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @return the associationPath
     */
    public String[] getAssociationPath() {
        return associationPath;
    }
    
    /**
     * Return the raw string that specifies that association path for this filter.
     */
    public String getAssociationPathString() {
        return associationPathString;
    }

    /**
     * @return the attributeName
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @return the targetClassName
     */
    public String getTargetClassName() {
        return targetClassName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(associationPath);
        result = prime * result + ((attributeName == null) ? 0 : attributeName.hashCode());
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((targetClassName == null) ? 0 : targetClassName.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Filter)) {
            return false;
        }
        Filter other = (Filter) obj;
        if (!Arrays.equals(associationPath, other.associationPath)) {
            return false;
        }
        if (attributeName == null) {
            if (other.attributeName != null) {
                return false;
            }
        } else if (!attributeName.equals(other.attributeName)) {
            return false;
        }
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (targetClassName == null) {
            if (other.targetClassName != null) {
                return false;
            }
        } else if (!targetClassName.equals(other.targetClassName)) {
            return false;
        }
        return true;
    }
}
