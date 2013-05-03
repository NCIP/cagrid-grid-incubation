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
/**
 */
package edu.emory.cci.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DeepCopier {
    private static final Logger myLogger = LogManager.getLogger(DeepCopier.class);
    
    /**
     * Private constructor so that there is no public default constructor
     */
    private DeepCopier() {
    }

    /**
     * Return a deep copy of the given object. The copying is done by
     * serialization/de-serialization.
     * 
     * @param obj The object to be copied
     * @return a deep copy of the object
     * @throws Exception if there is a problem.
     */
    public static Object deepCopy(Serializable obj) throws IOException {
        ObjectOutputStream oos = null;
        byte[] byteArray;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            byteArray = bos.toByteArray();
        } catch (IOException e) {
            String msg = "Problem in deepCopy while trying to serialze object.";
            myLogger.error(msg, e);
            throw (e);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
        ObjectInputStream ois = null;
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(byteArray);
            ois = new ObjectInputStream(bin); // F
            // return the new object
            return ois.readObject(); // G
        } catch (IOException e) {
            String msg = "Problem in deepCopy while trying to de-serialze object.";
            myLogger.error(msg, e);
            throw (e);
        } catch (ClassNotFoundException e) {
            String msg = "Problem in deepCopy while trying to de-serialze object.";
            myLogger.error(msg, e);
            IOException excp = new IOException(msg);
            excp.initCause(e);
            throw (excp);
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (ois != null) {
                ois.close();
            }
        }
    }

}
