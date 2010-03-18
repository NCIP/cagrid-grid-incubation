package org.cagrid.i2b2.domain;

public class Concept extends I2B2Type {

    private String conceptPath = null;
    private String name = null;
    private Double cdePublicId = null;
    private String cdeVersion = null;
    private String projectName = null;
    private Double projectVersion = null;
    private String encodingServiceURL = null;
    
    public Concept() {
        super();
    }

    public String getConceptPath() {
        return conceptPath;
    }

    public void setConceptPath(String conceptPath) {
        this.conceptPath = conceptPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCdePublicId() {
        return cdePublicId;
    }

    public void setCdePublicId(Double cdePublicId) {
        this.cdePublicId = cdePublicId;
    }

    public String getCdeVersion() {
        return cdeVersion;
    }

    public void setCdeVersion(String cdeVersion) {
        this.cdeVersion = cdeVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(Double projectVersion) {
        this.projectVersion = projectVersion;
    }

    public String getEncodingServiceURL() {
        return encodingServiceURL;
    }

    public void setEncodingServiceURL(String encodingServiceURL) {
        this.encodingServiceURL = encodingServiceURL;
    }

    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((cdePublicId == null) ? 0 : cdePublicId.hashCode());
        result = prime * result + ((cdeVersion == null) ? 0 : cdeVersion.hashCode());
        result = prime * result + ((conceptPath == null) ? 0 : conceptPath.hashCode());
        result = prime * result + ((encodingServiceURL == null) ? 0 : encodingServiceURL.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
        result = prime * result + ((projectVersion == null) ? 0 : projectVersion.hashCode());
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
        Concept other = (Concept) obj;
        if (cdePublicId == null) {
            if (other.cdePublicId != null) {
                return false;
            }
        } else if (!cdePublicId.equals(other.cdePublicId)) {
            return false;
        }
        if (cdeVersion == null) {
            if (other.cdeVersion != null) {
                return false;
            }
        } else if (!cdeVersion.equals(other.cdeVersion)) {
            return false;
        }
        if (conceptPath == null) {
            if (other.conceptPath != null) {
                return false;
            }
        } else if (!conceptPath.equals(other.conceptPath)) {
            return false;
        }
        if (encodingServiceURL == null) {
            if (other.encodingServiceURL != null) {
                return false;
            }
        } else if (!encodingServiceURL.equals(other.encodingServiceURL)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (projectName == null) {
            if (other.projectName != null) {
                return false;
            }
        } else if (!projectName.equals(other.projectName)) {
            return false;
        }
        if (projectVersion == null) {
            if (other.projectVersion != null) {
                return false;
            }
        } else if (!projectVersion.equals(other.projectVersion)) {
            return false;
        }
        return true;
    }
    
    
    
}
