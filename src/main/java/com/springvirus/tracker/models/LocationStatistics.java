package com.springvirus.tracker.models;

public class LocationStatistics {

    private String countryName;
    private int latestCases;
    private int deltaOfDay;
    private int latestDeaths;
    private int deltaOfDeaths;

    public LocationStatistics() {
    }

    public LocationStatistics(String countryName, int latestCases, int deltaOfDay, int latestDeaths, int deltaOfDeaths) {
        this.countryName = countryName;
        this.latestCases = latestCases;
        this.deltaOfDay = deltaOfDay;
        this.latestDeaths = latestDeaths;
        this.deltaOfDeaths = deltaOfDeaths;
    }

    public int getDeltaOfDeaths() {
        return deltaOfDeaths;
    }

    public void setDeltaOfDeaths(int deltaOfDeaths) {
        this.deltaOfDeaths = deltaOfDeaths;
    }

    public int getLatestDeaths() {
        return latestDeaths;
    }

    public void setLatestDeaths(int latestDeaths) {
        this.latestDeaths = latestDeaths;
    }

    public int getDeltaOfDay() {
        return deltaOfDay;
    }

    public void setDeltaOfDay(int deltaOfDay) {
        this.deltaOfDay = deltaOfDay;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getLatestCases() {
        return latestCases;
    }

    public void setLatestCases(int latestCases) {
        this.latestCases = latestCases;
    }

    @Override
    public String toString() {
        return "LocationStatistics{" +
                "country='" + countryName + '\'' +
                ", latestCases=" + latestCases +
                ", deltaOfDay=" + deltaOfDay +
                ", latestDeaths=" + latestDeaths +
                ", deltaOfDeaths=" + deltaOfDeaths +
                '}';
    }
}
