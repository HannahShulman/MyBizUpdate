package com.app.mybiz.Objects;

import java.io.Serializable;

/**
 * Created by hannashulmah on 22/01/2017.
 */

public class Location implements Serializable {
    public double latitude = 0;
    public double  longitude = 0;
    public String streetName = "";
    public int buildingNumber = 0;
    public String country = "";
    public String town = "";

    public Location() {
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }
}
