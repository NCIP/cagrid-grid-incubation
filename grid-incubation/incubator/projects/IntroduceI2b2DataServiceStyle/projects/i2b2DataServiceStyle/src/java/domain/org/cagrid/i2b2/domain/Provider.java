package org.cagrid.i2b2.domain;

public class Provider extends I2B2Type {

    private String name = null;


    public Provider() {
        super();
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        Provider other = (Provider) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
