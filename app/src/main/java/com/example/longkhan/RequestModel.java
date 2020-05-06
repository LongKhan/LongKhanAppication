package com.example.longkhan;

public class RequestModel {
    private String username, profile, tripCode, tripDoc, triptitle, currency;
    private double ori_amount, trans_amount;
    int position;

    public RequestModel(String username, double ori_amount, double trans_amount, String tripCode, String tripDoc,int position,String triptitle,String currency) {
        this.username = username;
        this.profile = profile;
        this.ori_amount = ori_amount ;
        this.trans_amount = trans_amount;
        this.tripCode = tripCode;
        this.tripDoc = tripDoc;
        this.position =position;
        this.currency = currency;
        this.triptitle = triptitle;
    }
    public String getUsername() {
        return username;
    }

    public String getProfile() {
        return profile;
    }

    public double getOri_amount() {
        return ori_amount;
    }

    public double getTrans_amount() {
        return trans_amount;
    }

    public String getTripCode() {
        return tripCode;
    }

    public String getTriptitle() {
        return triptitle;
    }

    public String getCurrency() {
        return currency;
    }

    public int getPosition() {
        return position;
    }

    public String getTripDoc() {
        return tripDoc;
    }
}
