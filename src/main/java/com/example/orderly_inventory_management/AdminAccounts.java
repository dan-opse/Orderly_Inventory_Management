package com.example.orderly_inventory_management;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class AdminAccounts {

    @BsonId
    private ObjectId id;
    public ObjectId getId() {
        return id;
    }
    public void setId(ObjectId id) {
        this.id = id;
    }

    private String Username, Password;

    public AdminAccounts(String username, String password) {
        Username = username;
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
