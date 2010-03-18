package org.cagrid.i2b2.domain;

import java.util.Date;


/**
 * Abstract base class of I2B2 data types that handles some basic information
 * about each data instance
 * 
 * @author David
 */
public abstract class I2B2Type {

    private Date updateDate = null;
    private Date downloadDate = null;
    private Date importDate = null;
    private String sourceSystemCd = null;


    public Date getUpdateDate() {
        return updateDate;
    }


    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


    public Date getDownloadDate() {
        return downloadDate;
    }


    public void setDownloadDate(Date downloadDate) {
        this.downloadDate = downloadDate;
    }


    public Date getImportDate() {
        return importDate;
    }


    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }


    public String getSourceSystemCd() {
        return sourceSystemCd;
    }


    public void setSourceSystemCd(String sourceSystemCd) {
        this.sourceSystemCd = sourceSystemCd;
    }


    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((downloadDate == null) ? 0 : downloadDate.hashCode());
        result = prime * result + ((importDate == null) ? 0 : importDate.hashCode());
        result = prime * result + ((sourceSystemCd == null) ? 0 : sourceSystemCd.hashCode());
        result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
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
        I2B2Type other = (I2B2Type) obj;
        if (downloadDate == null) {
            if (other.downloadDate != null) {
                return false;
            }
        } else if (!downloadDate.equals(other.downloadDate)) {
            return false;
        }
        if (importDate == null) {
            if (other.importDate != null) {
                return false;
            }
        } else if (!importDate.equals(other.importDate)) {
            return false;
        }
        if (sourceSystemCd == null) {
            if (other.sourceSystemCd != null) {
                return false;
            }
        } else if (!sourceSystemCd.equals(other.sourceSystemCd)) {
            return false;
        }
        if (updateDate == null) {
            if (other.updateDate != null) {
                return false;
            }
        } else if (!updateDate.equals(other.updateDate)) {
            return false;
        }
        return true;
    }
}
