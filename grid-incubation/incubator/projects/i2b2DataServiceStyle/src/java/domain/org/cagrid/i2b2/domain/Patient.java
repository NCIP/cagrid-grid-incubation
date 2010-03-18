package org.cagrid.i2b2.domain;

import java.util.Date;


/**
 * Patient
 * 
 * Describes an instance of Patient data in an I2B2 data repository
 * 
 * @author David
 */
public class Patient extends I2B2Type {

    private String vitalStatus = null;
    private Date birthDate = null;
    private Date deathDate = null;
    private String sex = null;
    private int ageInYears;
    private String language = null;
    private String race = null;
    private String zip = null;
    private String cityStateZipPath = null;


    public Patient() {
        super();
    }


    public String getVitalStatus() {
        return vitalStatus;
    }


    public void setVitalStatus(String vitalStatus) {
        this.vitalStatus = vitalStatus;
    }


    public Date getBirthDate() {
        return birthDate;
    }


    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }


    public Date getDeathDate() {
        return deathDate;
    }


    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }


    public String getSex() {
        return sex;
    }


    public void setSex(String sex) {
        this.sex = sex;
    }


    public int getAgeInYears() {
        return ageInYears;
    }


    public void setAgeInYears(int ageInYears) {
        this.ageInYears = ageInYears;
    }


    public String getLanguage() {
        return language;
    }


    public void setLanguage(String language) {
        this.language = language;
    }


    public String getRace() {
        return race;
    }


    public void setRace(String race) {
        this.race = race;
    }


    public String getZip() {
        return zip;
    }


    public void setZip(String zip) {
        this.zip = zip;
    }


    public String getCityStateZipPath() {
        return cityStateZipPath;
    }


    public void setCityStateZipPath(String cityStateZipPath) {
        this.cityStateZipPath = cityStateZipPath;
    }


    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ageInYears;
        result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
        result = prime * result + ((cityStateZipPath == null) ? 0 : cityStateZipPath.hashCode());
        result = prime * result + ((deathDate == null) ? 0 : deathDate.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((race == null) ? 0 : race.hashCode());
        result = prime * result + ((sex == null) ? 0 : sex.hashCode());
        result = prime * result + ((vitalStatus == null) ? 0 : vitalStatus.hashCode());
        result = prime * result + ((zip == null) ? 0 : zip.hashCode());
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
        Patient other = (Patient) obj;
        if (ageInYears != other.ageInYears) {
            return false;
        }
        if (birthDate == null) {
            if (other.birthDate != null) {
                return false;
            }
        } else if (!birthDate.equals(other.birthDate)) {
            return false;
        }
        if (cityStateZipPath == null) {
            if (other.cityStateZipPath != null) {
                return false;
            }
        } else if (!cityStateZipPath.equals(other.cityStateZipPath)) {
            return false;
        }
        if (deathDate == null) {
            if (other.deathDate != null) {
                return false;
            }
        } else if (!deathDate.equals(other.deathDate)) {
            return false;
        }
        if (language == null) {
            if (other.language != null) {
                return false;
            }
        } else if (!language.equals(other.language)) {
            return false;
        }
        if (race == null) {
            if (other.race != null) {
                return false;
            }
        } else if (!race.equals(other.race)) {
            return false;
        }
        if (sex == null) {
            if (other.sex != null) {
                return false;
            }
        } else if (!sex.equals(other.sex)) {
            return false;
        }
        if (vitalStatus == null) {
            if (other.vitalStatus != null) {
                return false;
            }
        } else if (!vitalStatus.equals(other.vitalStatus)) {
            return false;
        }
        if (zip == null) {
            if (other.zip != null) {
                return false;
            }
        } else if (!zip.equals(other.zip)) {
            return false;
        }
        return true;
    }
}
