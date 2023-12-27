package com.example.orderly_inventory_management;


import javafx.scene.control.CheckBox;

public class Items {


    private int id;
    private String Component, Value, Amount, DateLastBought, Link;

    public Items(int id, String component, String value, String amount, String dateLastBought, String link) {
        this.id = id;
        Component = component;
        Value = value;
        Amount = amount;
        DateLastBought = dateLastBought;
        Link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComponent() {
        return Component;
    }

    public void setComponent(String component) {
        Component = component;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDateLastBought() {
        return DateLastBought;
    }

    public void setDateLastBought(String dateLastBought) {
        DateLastBought = dateLastBought;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }
}
