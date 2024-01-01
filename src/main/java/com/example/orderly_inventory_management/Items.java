package com.example.orderly_inventory_management;


public class Items {

    private String Component, Value, Amount, DateLastBought, Link;

    public Items(String component, String value, String amount, String dateLastBought, String link) {
        Component = component;
        Value = value;
        Amount = amount;
        DateLastBought = dateLastBought;
        Link = link;
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
