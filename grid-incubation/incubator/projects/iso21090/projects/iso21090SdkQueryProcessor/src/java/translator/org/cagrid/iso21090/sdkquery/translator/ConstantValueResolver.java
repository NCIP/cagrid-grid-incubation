package org.cagrid.iso21090.sdkquery.translator;

import java.util.List;

public interface ConstantValueResolver {

    /**
     * Get the constant value object, if any, for an ISO data type
     * 
     * @param rootLevelClassName
     *      The root-level userland class (i.e. gov.nih.nci.cacoresdk.domain.other.datatype.DsetAdDataType)
     * @param propertyPath
     *      The attribute names through which we should navigate to check for a constant value
     *      (i.e. "value1", "item", "part0", "code")
     * @return
     *      The constant value of the specified property, or null if none is found
     */
    public Object getConstantValue(String rootLevelClassName, List<String> propertyPath);
}
