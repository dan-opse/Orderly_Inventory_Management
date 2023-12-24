package com.example.orderly_inventory_management;

public class SignOut {

    int id;
    private String FirstName, LastName, Component, DateSignedOut;

    public SignOut(int id, String FirstName, String LastName, String Component, String DateSignedOut) {
        this.id = id;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Component = Component;
        this.DateSignedOut = DateSignedOut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        this.FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getComponent() {
        return Component;
    }

    public void setComponent(String component) {
        this.Component = component;
    }

    public String getDateSignedOut() {
        return DateSignedOut;
    }

    public void setDateSignedOut(String dateSignedOut) {
        this.DateSignedOut = dateSignedOut;
    }
}
