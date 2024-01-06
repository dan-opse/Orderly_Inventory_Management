package com.example.orderly_inventory_management;

import javafx.beans.property.SimpleBooleanProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class Student {

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

    @BsonId
    private ObjectId id;
    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    public String Name, Item, Amount, Returned, Date;

    public Student(String name, String item, String amount, String returned, String date) {
        Name = name;
        Item = item;
        Amount = amount;
        Returned = returned;
        Date = date;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getReturned() {
        return Returned;
    }

    public void setReturned(String returned) {
        Returned = returned;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
