package com.example.longkhan;

public class DataestModel {
    String category;
    String date;
    int value;

    public DataestModel(String category, int y,String date) {
        this.category = category;
        this.value = y;
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
