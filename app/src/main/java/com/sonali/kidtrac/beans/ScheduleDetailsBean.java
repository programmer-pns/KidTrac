package com.sonali.kidtrac.beans;

public class ScheduleDetailsBean {
    private String fromTime, toTime;
    private double lat,lon;

    public ScheduleDetailsBean(String fromTime, String toTime, double lat, double lon) {
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.lat = lat;
        this.lon = lon;
    }
    private ScheduleDetailsBean(){

    }

    public String getFromTime() {
        return fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
