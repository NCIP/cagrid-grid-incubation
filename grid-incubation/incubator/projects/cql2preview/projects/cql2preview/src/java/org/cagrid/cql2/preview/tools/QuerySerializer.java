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
package org.cagrid.cql2.preview.tools;

import gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment;
import gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Child;
import gov.nih.nci.cacoresdk.domain.onetoone.multipleassociation.Parent;
import gov.nih.nci.cacoresdk.domain.other.levelassociation.Card;
import gov.nih.nci.cacoresdk.domain.other.levelassociation.Hand;
import gov.nih.nci.cacoresdk.domain.other.levelassociation.Suit;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cql2.aggregations.Aggregation;
import gov.nih.nci.cagrid.cql2.associations.AssociationPopulationSpecification;
import gov.nih.nci.cagrid.cql2.associations.NamedAssociation;
import gov.nih.nci.cagrid.cql2.associations.NamedAssociationList;
import gov.nih.nci.cagrid.cql2.attribute.AttributeValue;
import gov.nih.nci.cagrid.cql2.attribute.BinaryCQLAttribute;
import gov.nih.nci.cagrid.cql2.components.CQLAssociatedObject;
import gov.nih.nci.cagrid.cql2.components.CQLGroup;
import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.components.CQLTargetObject;
import gov.nih.nci.cagrid.cql2.components.GroupLogicalOperator;
import gov.nih.nci.cagrid.cql2.modifiers.CQLQueryModifier;
import gov.nih.nci.cagrid.cql2.modifiers.DistinctAttribute;
import gov.nih.nci.cagrid.cql2.predicates.BinaryPredicate;

import java.io.FileWriter;

import javax.xml.namespace.QName;

/** 
 *  QuerySerializer
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Feb 22, 2008 12:33:09 PM
 * @version $Id: QuerySerializer.java,v 1.2 2008/04/18 18:45:07 dervin Exp $ 
 */
public class QuerySerializer {
    
    public static final QName CQL_2_QNAME = new QName("http://CQL.caBIG/2/gov.nih.nci.cagrid.cql.Components", "CQLQuery");

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*
        // hand -> cards -> suit
        String hql = "From " + Hand.class.getName() + " as h " +
            "left join fetch h.cardCollection as cards left join fetch cards.suit " +
            "where h.cardCollection.id in (select c.id from " + Card.class.getName() + " c where c.Name = ? " +
                    "and c.suit.id in (select s.id from " + Suit.class.getName() + " s where s.name = ?))";
        
        
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName(Hand.class.getName());
        CQLAssociatedObject cardAssociation = new CQLAssociatedObject();
        cardAssociation.setClassName(Card.class.getName());
        cardAssociation.setSourceRoleName("cardCollection");
        CQLGroup cardGroup = new CQLGroup();
        cardGroup.setLogicalOperation(GroupLogicalOperator.AND);
        BinaryCQLAttribute cardName = new BinaryCQLAttribute();
        cardName.setName("Name");
        cardName.setPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue cardNameValue = new AttributeValue();
        cardNameValue.setStringValue("Ace");
        cardName.setAttributeValue(cardNameValue);
        cardGroup.setBinaryCQLAttribute(new BinaryCQLAttribute[] {cardName});
        CQLAssociatedObject suitAssociation = new CQLAssociatedObject();
        suitAssociation.setClassName(Suit.class.getName());
        suitAssociation.setSourceRoleName("suit");
        BinaryCQLAttribute suitName = new BinaryCQLAttribute();
        suitName.setName("name");
        suitName.setPredicate(BinaryPredicate.EQUAL_TO);
        AttributeValue suitNameValue = new AttributeValue();
        suitNameValue.setStringValue("Spade");
        suitName.setAttributeValue(suitNameValue);
        suitAssociation.setBinaryCQLAttribute(suitName);
        cardGroup.setCQLAssociatedObject(new CQLAssociatedObject[] {suitAssociation});
        cardAssociation.setCQLGroup(cardGroup);
        target.setCQLAssociatedObject(cardAssociation);
        query.setCQLTargetObject(target);
        
        AssociationPopulationSpecification spec = new AssociationPopulationSpecification();
        NamedAssociationList list = new NamedAssociationList();
        NamedAssociation[] firstLevel = new NamedAssociation[1];
        firstLevel[0] = new NamedAssociation();
        firstLevel[0].setRoleName("cardCollection");
        NamedAssociation[] secondLevel = new NamedAssociation[1];
        secondLevel[0] = new NamedAssociation();
        secondLevel[0].setRoleName("suit");
        firstLevel[0].setNamedAssociation(secondLevel);
        list.setNamedAssociation(firstLevel);
        spec.setNamedAssociationList(list);
        
        query.setAssociationPopulationSpecification(spec);
        */
        
        /*
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName(Payment.class.getName());
        query.setCQLTargetObject(target);
        CQLQueryModifier mods = new CQLQueryModifier();
        DistinctAttribute da = new DistinctAttribute();
        da.setAttributeName("id");
        da.setAggregation(Aggregation.COUNT);
        mods.setDistinctAttribute(da);
        query.setCQLQueryModifier(mods);
        */
        
        CQLQuery query = new CQLQuery();
        CQLTargetObject target = new CQLTargetObject();
        target.setClassName(Child.class.getName());
        AssociationPopulationSpecification population = new AssociationPopulationSpecification();
        NamedAssociation fatherAssociation = new NamedAssociation();
        fatherAssociation.setRoleName("father");
        population.setNamedAssociationList(
            new NamedAssociationList(new NamedAssociation[] {fatherAssociation}));
        query.setCQLTargetObject(target);
        query.setAssociationPopulationSpecification(population);
        
        try {
            FileWriter writer = new FileWriter("queries/simpleAssociationPopulation.xml");
            Utils.serializeObject(query, CQL_2_QNAME, writer);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
