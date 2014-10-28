package com.childlocator.xtreme.childemissor;

/**
 * Created by xtreme on 16-10-2014.
 */
public class Coordenada {

    public double lat;
    public double lng;
    public String timestamp;
    public float speed;

    public Coordenada (double coordLat, double coordLng, String coordTimestamp, float spd)
    {

        lat = coordLat;
        lng = coordLng;
        timestamp = coordTimestamp;
        speed = spd;

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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
