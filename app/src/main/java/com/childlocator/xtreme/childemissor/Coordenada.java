package com.childlocator.xtreme.childemissor;

/**
 * Created by xtreme on 16-10-2014.
 */
public class Coordenada {

    public double lat;
    public double lng;
    public String timestamp;

    public Coordenada (double coordLat, double coordLng, String coordTimestamp)
    {

        lat = coordLat;
        lng = coordLng;
        timestamp = coordTimestamp;

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
