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


public class Map {

    private String author = null;
    private String creator = null;
    private String curator = null;
    private String contributor = null;
    private Date createDate = null;
    private String license = null;
    private String licenseType = null;
    private Boolean isLicenseRestricted = null;
    private Date releaseDate = null;
    private String releaseURI = null;
    private String releaseVersion = null;
    private String rightsHolder = null;
    private String purpose = null;
    private String orgName = null;
    private String orgType = null;
    private String sourceSoftware = null;
    private String etlScriptUrl = null;
    private String releaseRepoName = null;
    private String targetConceptPath = null;
    
    private ArrayList<MapData> mapDataCollection = null;


    public Map() {

    }


    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public String getCreator() {
        return creator;
    }


    public void setCreator(String creator) {
        this.creator = creator;
    }


    public String getCurator() {
        return curator;
    }


    public void setCurator(String curator) {
        this.curator = curator;
    }


    public String getContributor() {
        return contributor;
    }


    public void setContributor(String contributor) {
        this.contributor = contributor;
    }


    public Date getCreateDate() {
        return createDate;
    }


    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }


    public String getLicense() {
        return license;
    }


    public void setLicense(String license) {
        this.license = license;
    }


    public String getLicenseType() {
        return licenseType;
    }


    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }


    public Boolean getIsLicenseRestricted() {
        return isLicenseRestricted;
    }


    public void setIsLicenseRestricted(Boolean isLicenseRestricted) {
        this.isLicenseRestricted = isLicenseRestricted;
    }


    public Date getReleaseDate() {
        return releaseDate;
    }


    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }


    public String getReleaseURI() {
        return releaseURI;
    }


    public void setReleaseURI(String releaseURI) {
        this.releaseURI = releaseURI;
    }


    public String getReleaseVersion() {
        return releaseVersion;
    }


    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }


    public String getRightsHolder() {
        return rightsHolder;
    }


    public void setRightsHolder(String rightsHolder) {
        this.rightsHolder = rightsHolder;
    }


    public String getPurpose() {
        return purpose;
    }


    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }


    public String getOrgName() {
        return orgName;
    }


    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }


    public String getOrgType() {
        return orgType;
    }


    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }


    public String getSourceSoftware() {
        return sourceSoftware;
    }


    public void setSourceSoftware(String sourceSoftware) {
        this.sourceSoftware = sourceSoftware;
    }


    public String getEtlScriptUrl() {
        return etlScriptUrl;
    }


    public void setEtlScriptUrl(String etlScriptUrl) {
        this.etlScriptUrl = etlScriptUrl;
    }


    public String getReleaseRepoName() {
        return releaseRepoName;
    }


    public void setReleaseRepoName(String releaseRepoName) {
        this.releaseRepoName = releaseRepoName;
    }


    public String getTargetConceptPath() {
        return targetConceptPath;
    }


    public void setTargetConceptPath(String targetConceptPath) {
        this.targetConceptPath = targetConceptPath;
    }
    
    
    public ArrayList<MapData> getMapDataCollection() {
        return mapDataCollection;
    }
    
    
    public void setMapDataCollection(ArrayList<MapData> mapDataCollection) {
        this.mapDataCollection = mapDataCollection;
    }


    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((contributor == null) ? 0 : contributor.hashCode());
        result = prime * result + ((createDate == null) ? 0 : createDate.hashCode());
        result = prime * result + ((creator == null) ? 0 : creator.hashCode());
        result = prime * result + ((curator == null) ? 0 : curator.hashCode());
        result = prime * result + ((etlScriptUrl == null) ? 0 : etlScriptUrl.hashCode());
        result = prime * result + ((isLicenseRestricted == null) ? 0 : isLicenseRestricted.hashCode());
        result = prime * result + ((license == null) ? 0 : license.hashCode());
        result = prime * result + ((licenseType == null) ? 0 : licenseType.hashCode());
        result = prime * result + ((mapDataCollection == null) ? 0 : mapDataCollection.hashCode());
        result = prime * result + ((orgName == null) ? 0 : orgName.hashCode());
        result = prime * result + ((orgType == null) ? 0 : orgType.hashCode());
        result = prime * result + ((purpose == null) ? 0 : purpose.hashCode());
        result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
        result = prime * result + ((releaseRepoName == null) ? 0 : releaseRepoName.hashCode());
        result = prime * result + ((releaseURI == null) ? 0 : releaseURI.hashCode());
        result = prime * result + ((releaseVersion == null) ? 0 : releaseVersion.hashCode());
        result = prime * result + ((rightsHolder == null) ? 0 : rightsHolder.hashCode());
        result = prime * result + ((sourceSoftware == null) ? 0 : sourceSoftware.hashCode());
        result = prime * result + ((targetConceptPath == null) ? 0 : targetConceptPath.hashCode());
        return result;
    }


    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Map other = (Map) obj;
        if (author == null) {
            if (other.author != null) {
                return false;
            }
        } else if (!author.equals(other.author)) {
            return false;
        }
        if (contributor == null) {
            if (other.contributor != null) {
                return false;
            }
        } else if (!contributor.equals(other.contributor)) {
            return false;
        }
        if (createDate == null) {
            if (other.createDate != null) {
                return false;
            }
        } else if (!createDate.equals(other.createDate)) {
            return false;
        }
        if (creator == null) {
            if (other.creator != null) {
                return false;
            }
        } else if (!creator.equals(other.creator)) {
            return false;
        }
        if (curator == null) {
            if (other.curator != null) {
                return false;
            }
        } else if (!curator.equals(other.curator)) {
            return false;
        }
        if (etlScriptUrl == null) {
            if (other.etlScriptUrl != null) {
                return false;
            }
        } else if (!etlScriptUrl.equals(other.etlScriptUrl)) {
            return false;
        }
        if (isLicenseRestricted == null) {
            if (other.isLicenseRestricted != null) {
                return false;
            }
        } else if (!isLicenseRestricted.equals(other.isLicenseRestricted)) {
            return false;
        }
        if (license == null) {
            if (other.license != null) {
                return false;
            }
        } else if (!license.equals(other.license)) {
            return false;
        }
        if (licenseType == null) {
            if (other.licenseType != null) {
                return false;
            }
        } else if (!licenseType.equals(other.licenseType)) {
            return false;
        }
        if (mapDataCollection == null) {
            if (other.mapDataCollection != null) {
                return false;
            }
        } else if (!CollectionUtils.isEqualCollection(mapDataCollection, other.mapDataCollection)) {
            return false;
        }
        if (orgName == null) {
            if (other.orgName != null) {
                return false;
            }
        } else if (!orgName.equals(other.orgName)) {
            return false;
        }
        if (orgType == null) {
            if (other.orgType != null) {
                return false;
            }
        } else if (!orgType.equals(other.orgType)) {
            return false;
        }
        if (purpose == null) {
            if (other.purpose != null) {
                return false;
            }
        } else if (!purpose.equals(other.purpose)) {
            return false;
        }
        if (releaseDate == null) {
            if (other.releaseDate != null) {
                return false;
            }
        } else if (!releaseDate.equals(other.releaseDate)) {
            return false;
        }
        if (releaseRepoName == null) {
            if (other.releaseRepoName != null) {
                return false;
            }
        } else if (!releaseRepoName.equals(other.releaseRepoName)) {
            return false;
        }
        if (releaseURI == null) {
            if (other.releaseURI != null) {
                return false;
            }
        } else if (!releaseURI.equals(other.releaseURI)) {
            return false;
        }
        if (releaseVersion == null) {
            if (other.releaseVersion != null) {
                return false;
            }
        } else if (!releaseVersion.equals(other.releaseVersion)) {
            return false;
        }
        if (rightsHolder == null) {
            if (other.rightsHolder != null) {
                return false;
            }
        } else if (!rightsHolder.equals(other.rightsHolder)) {
            return false;
        }
        if (sourceSoftware == null) {
            if (other.sourceSoftware != null) {
                return false;
            }
        } else if (!sourceSoftware.equals(other.sourceSoftware)) {
            return false;
        }
        if (targetConceptPath == null) {
            if (other.targetConceptPath != null) {
                return false;
            }
        } else if (!targetConceptPath.equals(other.targetConceptPath)) {
            return false;
        }
        return true;
    }
}
