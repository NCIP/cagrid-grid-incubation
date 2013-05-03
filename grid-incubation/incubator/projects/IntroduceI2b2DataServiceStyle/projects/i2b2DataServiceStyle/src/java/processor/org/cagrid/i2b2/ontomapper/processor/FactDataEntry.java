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
package org.cagrid.i2b2.ontomapper.processor;

import java.text.DateFormat;
import java.text.ParseException;

/**
 * FactDataEntry
 * Represents a single fact table data entry
 * 
 * @author David
 */
public class FactDataEntry {
    private String valueType = null;
    private String textValue = null;
    private Double numericValue = null;
    
    private Integer encounterNumber;
    private Integer patientNumber;
    
    public FactDataEntry(String valueType, String textValue, Double numericValue) {
        this(valueType, textValue, numericValue, null, null);
    }
    

    public FactDataEntry(String valueType, String textValue, Double numericValue, 
        Integer encounterNumber, Integer patientNumber) {
        this.valueType = valueType;
        this.textValue = textValue;
        this.numericValue = numericValue;
        this.encounterNumber = encounterNumber;
        this.patientNumber = patientNumber;
    }


    public String getValueType() {
        return valueType;
    }


    public String getTextValue() {
        return textValue;
    }


    public Double getNumericValue() {
        return numericValue;
    }


    public Integer getEncounterNumber() {
        return encounterNumber;
    }


    public Integer getPatientNumber() {
        return patientNumber;
    }
    
    
    public Object getTypedValue() throws ParseException {
        if ("T".equals(getValueType())) {
            return getTextValue();
        } else if ("D".equals(getValueType())) {
            return DateFormat.getDateTimeInstance().parse(getTextValue());
        } else if ("N".equals(getValueType())) {
            return getNumericValue();
        } else if ("B".equals(getValueType())) { // Boolean?
            return Boolean.valueOf(getTextValue());
        }
        return null;
    }
    
    
    public String getActualValueAsString() {
        String realValue = null;
        // T = text
        // N = number (double????)
        // D = date - time
        // B = ? (no actual values with this one)
        // @ = no data
        if ("T".equals(getValueType()) || "D".equals(getValueType()) || "B".equals(getValueType())) {
            realValue = getTextValue();
        } else if ("N".equals(getValueType())) {
            realValue = String.valueOf(getNumericValue());
        }
        I2B2QueryProcessor.LOG.debug("Unknown value type: " + getValueType() + ", returning null value");
        return realValue;
    }
}
