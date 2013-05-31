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
package org.cagrid.i2b2.domain;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.collections.CollectionUtils;


public class Visit extends I2B2Type {

    private String activeStatus = null;
    private Date startDate = null;
    private Date endDate = null;
    private String inOut = null;
    private String location = null;
    private String locationPath = null;
    
    private ArrayList<MapData> mapDataCollection = null;


    public Visit() {
        super();
    }


    public String getActiveStatus() {
        return activeStatus;
    }


    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }


    public Date getStartDate() {
        return startDate;
    }


    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }


    public Date getEndDate() {
        return endDate;
    }


    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public String getInOut() {
        return inOut;
    }


    public void setInOut(String inOut) {
        this.inOut = inOut;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public String getLocationPath() {
        return locationPath;
    }


    public void setLocationPath(String locationPath) {
        this.locationPath = locationPath;
    }
    
    
    public ArrayList<MapData> getMapDataCollection() {
        return mapDataCollection;
    }
    
    
    public void setMapDataCollection(ArrayList<MapData> mapDataCollection) {
        this.mapDataCollection = mapDataCollection;
    }


    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((activeStatus == null) ? 0 : activeStatus.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((inOut == null) ? 0 : inOut.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((locationPath == null) ? 0 : locationPath.hashCode());
        result = prime * result + ((mapDataCollection == null) ? 0 : mapDataCollection.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        return result;
    }


    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Visit other = (Visit) obj;
        if (activeStatus == null) {
            if (other.activeStatus != null) {
                return false;
            }
        } else if (!activeStatus.equals(other.activeStatus)) {
            return false;
        }
        if (endDate == null) {
            if (other.endDate != null) {
                return false;
            }
        } else if (!endDate.equals(other.endDate)) {
            return false;
        }
        if (inOut == null) {
            if (other.inOut != null) {
                return false;
            }
        } else if (!inOut.equals(other.inOut)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (locationPath == null) {
            if (other.locationPath != null) {
                return false;
            }
        } else if (!locationPath.equals(other.locationPath)) {
            return false;
        }
        if (mapDataCollection == null) {
            if (other.mapDataCollection != null) {
                return false;
            }
        } else if (!CollectionUtils.isEqualCollection(mapDataCollection, other.mapDataCollection)) {
            return false;
        }
        if (startDate == null) {
            if (other.startDate != null) {
                return false;
            }
        } else if (!startDate.equals(other.startDate)) {
            return false;
        }
        return true;
    }
}
