package com.example.orderly_inventory_management;

import javafx.beans.property.SimpleBooleanProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Items {

    // For 'select_col'
    private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
    public SimpleBooleanProperty selectedProperty() {
        return selected;
    }

    // For retrieving 'ObjectId'
    @BsonId
    private ObjectId id;
    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    private String Component, Value, Amount, DateLastBought, Link;

    public Items() {}

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
