package org.cagrid.iso21090.sdkquery.translator;

import gov.nih.nci.iso21090.Bl;
import gov.nih.nci.iso21090.Ed;
import gov.nih.nci.iso21090.St;
import gov.nih.nci.iso21090.Tel;
import gov.nih.nci.iso21090.TelPerson;

import java.util.HashMap;
import java.util.Map;

import org.iso._21090.Ad;
import org.iso._21090.BL;
import org.iso._21090.BlNonNull;
import org.iso._21090.CD;
import org.iso._21090.DSetAd;
import org.iso._21090.DSetCd;
import org.iso._21090.DSetII;
import org.iso._21090.DSetTel;
import org.iso._21090.ED;
import org.iso._21090.EN;
import org.iso._21090.EdText;
import org.iso._21090.EnOn;
import org.iso._21090.EnPn;
import org.iso._21090.INT;
import org.iso._21090.IVLINT;
import org.iso._21090.IVLPQ;
import org.iso._21090.IVLREAL;
import org.iso._21090.IVLTS;
import org.iso._21090.Ii;
import org.iso._21090.PQ;
import org.iso._21090.Real;
import org.iso._21090.SC;
import org.iso._21090.ST;
import org.iso._21090.StNt;
import org.iso._21090.TEL;
import org.iso._21090.TELPerson;
import org.iso._21090.TS;
import org.iso._21090.TelEmail;
import org.iso._21090.TelPhone;
import org.iso._21090.TelUrl;

/**
 * DatatypeFlavor
 * Describes the queried data type to the CQL to HQL translator
 * so it can appropriately generate queries
 * 
 * @author David
 */
public enum DatatypeFlavor {

    STANDARD,                                                               // your.domain.Person
    COMPLEX_WITH_SIMPLE_CONTENT,                                            // org.iso._21090.II
    COMPLEX_WITH_NESTED_COMPLEX,                                            // org.iso._21090.SC
    COMPLEX_WITH_COLLECTION_OF_COMPLEX,                                     // org.iso._21090.EN
    COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT,                              // org.iso._21090.DSET<CD>
    COLLECTION_OF_COMPLEX_WITH_COLLECTION_OF_COMPLEX_WITH_SIMPLE_CONTENT    // org.iso._21090.DSET<AD>
    ;
    
    private static final Map<Class<?>, DatatypeFlavor> CLASS_FLAVORS = new HashMap<Class<?>, DatatypeFlavor>();
    static {
        CLASS_FLAVORS.put(BL.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Bl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(BlNonNull.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.BlNonNull.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(ST.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(St.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(StNt.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.StNt.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Ii.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.Ii.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TEL.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Tel.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TELPerson.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelPerson.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelUrl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelUrl.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelPhone.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelPhone.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(TelEmail.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(gov.nih.nci.iso21090.TelEmail.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(ED.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(Ed.class, COMPLEX_WITH_SIMPLE_CONTENT);
        CLASS_FLAVORS.put(EdText.class, DatatypeFlavor.COMPLEX_WITH_SIMPLE_CONTENT);
        
        CLASS_FLAVORS.put(CD.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(SC.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(INT.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(Real.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(TS.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        // CLASS_FLAVORS.put(PQV.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(PQ.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(IVLTS.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(IVLPQ.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        // CLASS_FLAVORS.put(IVLPQV.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(IVLREAL.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        CLASS_FLAVORS.put(IVLINT.class, DatatypeFlavor.COMPLEX_WITH_NESTED_COMPLEX);
        
        CLASS_FLAVORS.put(Ad.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EN.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EnOn.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(EnPn.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        
        CLASS_FLAVORS.put(DSetII.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(DSetTel.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(DSetCd.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
        CLASS_FLAVORS.put(DSetAd.class, DatatypeFlavor.COMPLEX_WITH_COLLECTION_OF_COMPLEX);
    }
    
    public static DatatypeFlavor getFlavorOfClass(Class<?> clazz) {
        DatatypeFlavor flavor = CLASS_FLAVORS.get(clazz);
        if (flavor == null) {
            flavor = STANDARD;
        }
        return flavor;
    }
}
