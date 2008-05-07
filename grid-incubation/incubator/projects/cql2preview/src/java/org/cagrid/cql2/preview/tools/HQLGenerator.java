package org.cagrid.cql2.preview.tools;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.DomainTypesInformationUtil;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.cagrid.cql2.preview.processor.CQL2ParameterizedHQL;
import org.cagrid.cql2.preview.processor.ParameterizedHqlQuery;

/** 
 *  HQLGenerator
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Feb 14, 2008 11:26:11 AM
 * @version $Id: HQLGenerator.java,v 1.2 2008/04/08 13:56:44 dervin Exp $ 
 */
public class HQLGenerator {
    
    public static void main(String[] args) {
        try {
            FileReader typesInfoReader = new FileReader("resources/sdkExampleDomainTypes.xml");
            DomainTypesInformation typesInfo = DomainTypesInformationUtil.deserializeDomainTypesInformation(typesInfoReader);
            typesInfoReader.close();
            
            FileReader modelReader = new FileReader("resources/sdkExampleDomainModel.xml");
            DomainModel model = (DomainModel) Utils.deserializeObject(modelReader, DomainModel.class);
            modelReader.close();
            
            CQL2ParameterizedHQL translator = new CQL2ParameterizedHQL(typesInfo, model);
            
            // String queryFilename = "queries/handCardSuit_associationPopulationByDepth.xml";
            // String queryFilename = "queries/handCardSuit_associationPopulation.xml";
            // String queryFilename = "queries/handCardSuit_associationPopulationInfiniteDepth.xml";
            String queryFilename = "queries/countAllPayments.xml";
            FileReader queryReader = new FileReader(queryFilename);
            CQLQuery query = (CQLQuery) Utils.deserializeObject(queryReader, CQLQuery.class);
            queryReader.close();
            
            long start = System.currentTimeMillis();
            // ParameterizedHqlQuery hql = translator.convertToHql(query);
            String rawHql = "select count(p.id) From gov.nih.nci.cacoresdk.domain.inheritance.childwithassociation.Payment as p where p.class = ?";
            List<Object> parameters = new ArrayList<Object>();
            // parameters.add(Integer.valueOf(0));
            // parameters.add(Integer.valueOf(2));
            
            ParameterizedHqlQuery hql = new ParameterizedHqlQuery(rawHql, parameters);
            System.out.println("Conversion from CQL to HQL in " + (System.currentTimeMillis() - start) + " ms");
            System.out.println(hql);
            
            ApplicationService service = ApplicationServiceProvider.getApplicationServiceFromUrl("http://kramer.bmi.ohio-state.edu:8080/example40");
            System.out.println("Querying");
            List results = service.query(new HQLCriteria(hql.getHql(), hql.getParameters()));
            /* Hands of Cards with Suits
            for (Object o : results) {
                Hand hand = (Hand) o;
                System.out.println("Hand: " + hand.getId());
                Collection<Card> cards = hand.getCardCollection();
                for (Card card : cards) {
                    System.out.println("\tCard: " + card.getName());
                    Suit suit = card.getSuit();
                    if (suit != null) {
                        System.out.println("\t\tSuit: " + suit.getName());
                    }
                }
            }
            */
            Long count = (Long) results.get(0);
            System.out.println("Found " + count.longValue() + " payments");
            System.out.println("Done");
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
