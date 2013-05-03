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
package org.cagrid.cql2.preview.tools;

import gov.nih.nci.cacoresdk.domain.other.levelassociation.Card;
import gov.nih.nci.cacoresdk.domain.other.levelassociation.Hand;
import gov.nih.nci.cacoresdk.domain.other.levelassociation.Suit;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** 
 *  Joiner
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Feb 22, 2008 3:27:34 PM
 * @version $Id: Joiner.java,v 1.1 2008/04/03 18:14:28 dervin Exp $ 
 */
public class Joiner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            ApplicationService service = ApplicationServiceProvider.getApplicationServiceFromUrl("http://kramer.bmi.ohio-state.edu:8080/example40");
                        
            /*
            // computers with hard drives
            String hql = "From gov.nih.nci.cacoresdk.domain.onetomany.bidirectional.Computer as __TargetAlias__ " +
                "left join fetch __TargetAlias__.hardDriveCollection where __TargetAlias__.hardDriveCollection.id is not null";
            */
            
            // hand -> cards -> suit
            String hql = "From " + Hand.class.getName() + " as h " +
                "left join fetch h.cardCollection as cards left join fetch cards.suit " +
                "where h.cardCollection.id in (select c.id from " + Card.class.getName() + " c where c.Name = ? " +
                        "and c.suit.id in (select s.id from " + Suit.class.getName() + " s where s.name = ?))";
            
            List properties = new ArrayList();
            properties.add("Ace");
            properties.add("Spade");
            
            List results = service.query(new HQLCriteria(hql, properties));
            
            /* 
            // computers and hard drives
            for (Object o : results) {
                Computer c = (Computer) o;
                System.out.println("Computer: " + c.getType());
                Collection<HardDrive> drives = c.getHardDriveCollection();
                for (HardDrive d : drives) {
                    System.out.println("\tHard Drive size: " + d.getSize());
                    Computer comp = d.getComputer();
                    System.out.println("\t\tComputer: " + comp.getType());
                }
            }
            */
            
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
