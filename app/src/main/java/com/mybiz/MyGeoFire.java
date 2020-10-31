package com.mybiz;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by itzikalgrisi on 23/02/2017.
 */

public class MyGeoFire implements Serializable{
    String g;
    ArrayList<Double> l = new ArrayList<>();

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public ArrayList<Double> getL() {
        return l;
    }

    public void setL(ArrayList<Double> l) {
        this.l = l;
    }

    @Override
    public String toString() {
        return "MyGeoFire{" +
                "g='" + g + '\'' +
                ", l=" + l +
                '}';
    }
}
