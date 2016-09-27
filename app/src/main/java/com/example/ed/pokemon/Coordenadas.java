package com.example.ed.pokemon;

/**
 * Created by ED on 13/9/2016.
 */
public class Coordenadas {
    private String lt,lng;
    public Coordenadas(String lt, String lng) {
        this.lng=lng;
        this.lt=lt;
    }

    public String getLatitude() {
        return lt;
    }

    public String getLongitude() {
        return lng;
    }
}
