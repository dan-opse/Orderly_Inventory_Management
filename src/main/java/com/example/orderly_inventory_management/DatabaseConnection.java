package com.example.orderly_inventory_management;

import java.sql.*;

public class DatabaseConnection {
    Connection databaseLink;


    public Connection getUserInfoConnection() {
        String databaseName = "userInformation";
        String databaseUser = "root";
        String databasePassword = "5f2B7o?35j";
        String url = "jdbc:mysql://localhost/" + databaseName;


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink = DriverManager.getConnection(url, databaseUser, databasePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return databaseLink;
    }
}
