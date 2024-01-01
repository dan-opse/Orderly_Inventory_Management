package com.example.orderly_inventory_management;

import com.mongodb.ConnectionString;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBConnection {
    MongoClient client = MongoClients.create("mongodb+srv://root:8298680745@cluster0.rx9njg2.mongodb.net/?retryWrites=true&w=majority");
    MongoDatabase db = client.getDatabase("ORDERLY");
}

