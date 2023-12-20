package com.example.orderly_inventory_management;


public class Items {


    int id;
    String Component, Value, Amount, DateLastBought, Link;


    // This follows the query fields or Table View columns sequence
    public Items(int id, String component, String value, String amount, String DateLastBought, String link) {
        this.id = id;
        this.Component = component;
        this.Value = value;
        this.Amount = amount;
        this.DateLastBought = DateLastBought;
        this.Link = link;
    }


    // Getters


    public int getId(){
        return id;
    }


    public String getComponent() {
        return Component;
    }


    public String getValue() {
        return Value;
    }


    public String getAmount() {
        return Amount;
    }


    public String getDateLastBought() {
        return DateLastBought;
    }


    public String getLink() {
        return Link;
    }


    // Setters


    public void setId(int id) {
        this.id = id;
    }


    public void setComponent(String component) {
        this.Component = component;
    }


    public void setValue(String value) {
        this.Value = value;
    }


    public void setAmount(String amount) {
        this.Amount = amount;
    }


    public void setDateLastBought(String dateLastBought) {
        this.DateLastBought = dateLastBought;
    }


    public void setLink(String link) {
        this.Link = link;
    }
}
