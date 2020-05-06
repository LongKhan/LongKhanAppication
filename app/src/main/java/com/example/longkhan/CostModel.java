package com.example.longkhan;


import java.util.ArrayList;

public class CostModel {
    String cost_name,costDoc,cost_adder,this_cost,receipt,detail, trip_code,trip_docid, trip_currency;
    ArrayList <String> note = new ArrayList<>();


    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public CostModel(String cost_name, String this_cost, String costDoc, String cost_adder, String receipt,String detail,ArrayList <String> note,String trip_code,String trip_docid, String trip_currency) {
        this.cost_name = cost_name;
        this.this_cost = this_cost;
        this.detail = detail;
        this.costDoc = costDoc;
        this.cost_adder =cost_adder;
        this.receipt =receipt;
        this.note = note;
        this.trip_code = trip_code;
        this.trip_docid = trip_docid;
        this.trip_currency = trip_currency;
    }

    public String getTrip_docid() {
        return trip_docid;
    }

    public void setTrip_docid(String trip_docid) {
        this.trip_docid = trip_docid;
    }

    public String getTrip_code() {
        return trip_code;
    }

    public void setTrip_code(String trip_code) {
        this.trip_code = trip_code;
    }

    public ArrayList<String> getNote() {
        return note;
    }

    public void setNote(ArrayList<String> note) {
        this.note = note;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getCost_name() {
        return cost_name;
    }

    public void setCost_name(String cost_name) {
        this.cost_name = cost_name;
    }

    public String getCostDoc() {
        return costDoc;
    }

    public void setCostDoc(String costDoc) {
        this.costDoc = costDoc;
    }

    public String getCost_adder() {
        return cost_adder;
    }

    public void setCost_adder(String cost_adder) {
        this.cost_adder = cost_adder;
    }

    public String getThis_cost() {
        return this_cost;
    }

    public void setThis_cost(String this_cost) {
        this.this_cost = this_cost;
    }

    public String getTrip_currency() {
        return trip_currency;
    }
}
