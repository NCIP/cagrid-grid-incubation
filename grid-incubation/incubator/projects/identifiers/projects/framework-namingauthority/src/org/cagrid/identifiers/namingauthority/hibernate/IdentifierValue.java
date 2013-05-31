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
package org.cagrid.identifiers.namingauthority.hibernate;

public class IdentifierValue {
    private Long id;
    private String name;
    private String type;
    private String data;
    
    public IdentifierValue() {}
    

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getData() {
    	return data;
    }
    
    public void setData(String data) {
    	this.data = data;
    }
}
