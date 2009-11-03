package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlquery.Predicate;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAssociatedObject;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLObject;
import org.cagrid.cql2.CQLQueryModifier;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.jdom.Document;
import org.jdom.Element;

public class CQL2toCQL1Converter {
    
    private static Map<BinaryPredicate, Predicate> binaryPredicateConversion = null;
    private static Map<UnaryPredicate, Predicate> unaryPredicateConversion = null;
    static {
        binaryPredicateConversion = new HashMap<BinaryPredicate, Predicate>();
        unaryPredicateConversion = new HashMap<UnaryPredicate, Predicate>();
        binaryPredicateConversion.put(BinaryPredicate.EQUAL_TO, Predicate.EQUAL_TO);
        binaryPredicateConversion.put(BinaryPredicate.GREATER_THAN, Predicate.GREATER_THAN);
        binaryPredicateConversion.put(BinaryPredicate.GREATER_THAN_EQUAL_TO, Predicate.GREATER_THAN_EQUAL_TO);
        binaryPredicateConversion.put(BinaryPredicate.LESS_THAN, Predicate.LESS_THAN);
        binaryPredicateConversion.put(BinaryPredicate.LESS_THAN_EQUAL_TO, Predicate.LESS_THAN_EQUAL_TO);
        binaryPredicateConversion.put(BinaryPredicate.LIKE, Predicate.LIKE);
        binaryPredicateConversion.put(BinaryPredicate.NOT_EQUAL_TO, Predicate.NOT_EQUAL_TO);
        unaryPredicateConversion.put(UnaryPredicate.IS_NOT_NULL, Predicate.IS_NOT_NULL);
        unaryPredicateConversion.put(UnaryPredicate.IS_NULL, Predicate.IS_NULL);
    }
    
    
    public CQL2toCQL1Converter() {
    }
    
    
    public CQLQuery convertToCql1Query(org.cagrid.cql2.CQLQuery cql2Query) throws QueryConversionException {
        assertNoAssociationPopulation(cql2Query);
        assertValidAggregation(cql2Query);
        
        CQLQuery query = new CQLQuery();
        
        return query;
    }
    
    
    private void assertNoAssociationPopulation(org.cagrid.cql2.CQLQuery query) throws QueryConversionException {
        if (query.getAssociationPopulationSpecification() != null) {
            throw new QueryConversionException("Association population is not supported in CQL 1");
        }
    }
    
    
    private void assertValidAggregation(org.cagrid.cql2.CQLQuery query) throws QueryConversionException {
        CQLQueryModifier mods = query.getCQLQueryModifier();
        if (mods != null && mods.getDistinctAttribute() != null && mods.getDistinctAttribute().getAggregation() != null) {
            Aggregation agg = mods.getDistinctAttribute().getAggregation();
            if (!agg.equals(Aggregation.COUNT)) {
                throw new QueryConversionException("CQL 1 does not support aggregations of the type " + agg.getValue());
            }
        }
    }
    
    
    private Object convertObject(CQLObject cql2Object) throws QueryConversionException {
        if (cql2Object.get_instanceof() != null) {
            throw new QueryConversionException("CQL 1 does not support \"instanceof\" operations");
        }
        Object o = new Object();
        o.setName(cql2Object.getClassName());
        if (cql2Object.getCQLAssociatedObject() != null) {
            Association assoc = convertAssociation(cql2Object.getCQLAssociatedObject());
            o.setAssociation(assoc);
        }
        if (cql2Object.getCQLAttribute() != null) {
            Attribute attr = convertAttribute(cql2Object.getCQLAttribute());
            o.setAttribute(attr);
        }
        if (cql2Object.getCQLGroup() != null) {
            Group g = convertGroup(cql2Object.getCQLGroup());
            o.setGroup(g);
        }
        return o;
    }
    
    
    private Association convertAssociation(CQLAssociatedObject cql2Association) throws QueryConversionException {
        Object base = convertObject(cql2Association);
        Association assoc = new Association();
        assoc.setAssociation(base.getAssociation());
        assoc.setAttribute(base.getAttribute());
        assoc.setGroup(base.getGroup());
        assoc.setName(base.getName());
        assoc.setRoleName(cql2Association.getEndName());
        return assoc;
    }
    
    
    private Attribute convertAttribute(CQLAttribute cql2Attribute) throws QueryConversionException {
        Attribute attr = new Attribute();
        attr.setName(cql2Attribute.getName());
        Predicate pred = null;
        if (cql2Attribute.getBinaryPredicate() != null) {
            pred = binaryPredicateConversion.get(cql2Attribute.getBinaryPredicate());
            attr.setValue(getAttributeValueAsString(cql2Attribute.getAttributeValue()));
        } else {
            pred = unaryPredicateConversion.get(cql2Attribute.getUnaryPredicate());
        }
        attr.setPredicate(pred);
        return attr;
    }
    
    
    private String getAttributeValueAsString(AttributeValue value) throws QueryConversionException {
        Document valueDoc = null;
        try {
            StringWriter writer = new StringWriter();
            Utils.serializeObject(value, AttributeValue.getTypeDesc().getXmlType(), writer);
            valueDoc = XMLUtilities.stringToDocument(writer.getBuffer().toString());
        } catch (Exception ex) {
            throw new QueryConversionException("Error serializing attribute value: " + ex.getMessage(), ex);
        }
        String string = null;
        List<?> valueHolderElements = valueDoc.getRootElement().getChildren();
        Iterator<?> valueHolderIter = valueHolderElements.iterator();
        while (valueHolderIter.hasNext()) {
            Element holderElem = (Element) valueHolderIter.next();
            if (holderElem.getText() != null && holderElem.getText().length() != 0) {
                string = holderElem.getText();
                break;
            }
        }
        return string;
    }
    
    
    private Group convertGroup(CQLGroup cql2Group) throws QueryConversionException {
        Group group = new Group();
        group.setLogicRelation(cql2Group.getLogicalOperation() == GroupLogicalOperator.AND 
            ? LogicalOperator.AND : LogicalOperator.OR);
        if (cql2Group.getCQLAssociatedObject() != null) {
            Association[] associations = new Association[cql2Group.getCQLAssociatedObject().length];
            for (int i = 0; i < cql2Group.getCQLAssociatedObject().length; i++) {
                associations[i] = convertAssociation(cql2Group.getCQLAssociatedObject(i));
            }
            group.setAssociation(associations);
        }
        if (cql2Group.getCQLAttribute() != null) {
            Attribute[] attributes = new Attribute[cql2Group.getCQLAttribute().length];
            for (int i = 0; i < cql2Group.getCQLAttribute().length; i++) {
                attributes[i] = convertAttribute(cql2Group.getCQLAttribute(i));
            }
            group.setAttribute(attributes);
        }
        if (cql2Group.getCQLGroup() != null) {
            Group[] groups = new Group[cql2Group.getCQLGroup().length];
            for (int i = 0; i < cql2Group.getCQLGroup().length; i++) {
                groups[i] = convertGroup(cql2Group.getCQLGroup(i));
            }
            group.setGroup(groups);
        }
        return group;
    }
}
