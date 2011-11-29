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