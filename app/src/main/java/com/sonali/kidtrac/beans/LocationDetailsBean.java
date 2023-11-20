package com.sonali.kidtrac.beans;

public class LocationDetailsBean {
    private String latitude;
    private String longitude;
    private long time;
    private LocationDetailsBean(){

    }

    public LocationDetailsBean(String latitude, String longitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
    public long getTime() {
        return time;
    }

}
