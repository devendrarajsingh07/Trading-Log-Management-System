package com.tradinglog.app;

import com.tradinglog.app.service.DBConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection con = DBConnection.getConnection();
            System.out.println(con != null ? "Database Connected Successfully!" : "Connection Failed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}