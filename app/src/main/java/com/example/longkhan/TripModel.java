package com.example.longkhan;

public class TripModel {
    private String tripTitle;
    private String urlProfile;
    String doctripid;
    public TripModel(String title,String url,String doctripid) {
        this.tripTitle = title;
        this.urlProfile = url;
        this.doctripid = doctripid;
    }
    public String getTripTitle() {
        return tripTitle;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public String getDoctripid() {
        return doctripid;
    }
}
