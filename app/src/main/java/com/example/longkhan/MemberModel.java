package com.example.longkhan;

public class MemberModel {
    private String usrname;
    private String urlProfile;
    String  amout_peruser, usr_currency;

    public MemberModel(String usrName, String urlProfile, String amout_peruser) {
        this.usrname = usrName;
        this.urlProfile = urlProfile;
        this.amout_peruser = amout_peruser;
    }

    public String getUsrname() {
        return usrname;
    }

    public void setUsrname(String usrname) {
        this.usrname = usrname;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getAmout_peruser() {
        return amout_peruser;
    }

    public void setAmout_peruser(String amout_peruser) {
        this.amout_peruser = amout_peruser;
    }

    public String getUsr_currency() {
        return usr_currency;
    }

    public MemberModel(String tripTitle, String urlProfile, String amout_peruser, String usr_currency) {
        this.usrname = tripTitle;
        this.urlProfile = urlProfile;
        this.amout_peruser = amout_peruser;
        this.usr_currency = usr_currency;
    }
}
