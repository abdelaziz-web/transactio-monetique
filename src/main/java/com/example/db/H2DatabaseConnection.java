package com.example.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

public class H2DatabaseConnection {
    // JDBC URL, username and password of H2 database
    private static final String JDBC_URL = "jdbc:h2:file:./testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    private static final String USER = "sa";
    private static final String PASSWORD = ""; // Empty string for no password

    private static Connection conn = null;

    private H2DatabaseConnection() {} // Private constructor to prevent instantiation

    public static Connection getConnection() {
        if (conn == null) {
            try {
                conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
                conn.setAutoCommit(false); // Set autocommit to false for manual transaction control
                System.out.println("Database connection established.");
            } catch (SQLException e) {
                System.out.println("Error connecting to the database: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing the database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void insertISOMessage(ISOMsg message) throws ISOException {
        System.out.println("JDBC URL: " + JDBC_URL);

        String sql = "INSERT INTO ISO8583_AUTH_REQUEST (f000, f002, f003, f004, f007, f011, f014, f018, f022, f025, f035, f037, f041, f042, f049) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, message.getMTI());
            pstmt.setString(2, message.getString(2));
            pstmt.setString(3, message.getString(3));
            pstmt.setString(4, message.getString(4));
            pstmt.setString(5, message.getString(7));
            pstmt.setString(6, message.getString(11));
            pstmt.setString(7, message.getString(14));
            pstmt.setString(8, message.getString(18));
            pstmt.setString(9, message.getString(22));
            pstmt.setString(10, message.getString(25));
            pstmt.setString(11, message.getString(35));
            pstmt.setString(12, message.getString(37));
            pstmt.setString(13, message.getString(41));
            pstmt.setString(14, message.getString(42));
            pstmt.setString(15, message.getString(49));

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("A new message was inserted successfully!");
                getConnection().commit();
                verifyInsertion();
            } else {
                System.out.println("Insertion failed. No rows affected.");
                getConnection().rollback();
            }
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Error during insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }

   
    public static void insertISOResponse(ISOMsg response) throws ISOException {
        System.out.println("JDBC URL: " + JDBC_URL);
    
        String sql = "INSERT INTO ISO8583_AUTH_RESPONSE (f000, f002, f003, f004, f007, f011, f012, f013, f014, f015, f018, f022, f025, f032, f035, f037, f038, f039, f041, f042, f043, f044, f048, f049, f054, f055, f060, f062, f063) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            int[] fields = {0, 2, 3, 4, 7, 11, 12, 13, 14, 15, 18, 22, 25, 32, 35, 37, 38, 39, 41, 42, 43, 44, 48, 49, 54, 55, 60, 62, 63};
            
            for (int i = 0; i < fields.length; i++) {
                pstmt.setString(i + 1, response.hasField(fields[i]) ? response.getString(fields[i]) : "");
            }
    
            int rowsAffected = pstmt.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("A new response was inserted successfully!");
                getConnection().commit();
                verifyResponseInsertion();
            } else {
                System.out.println("Response insertion failed. No rows affected.");
                getConnection().rollback();
            }
        } catch (SQLException e) {
            try {
                getConnection().rollback();
            } catch (SQLException rollbackEx) {
                System.out.println("Error during rollback: " + rollbackEx.getMessage());
            }
            System.out.println("Error during response insertion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verifyResponseInsertion() throws SQLException {
        String verifySQL = "SELECT * FROM ISO8583_AUTH_RESPONSE ORDER BY ID DESC LIMIT 1";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(verifySQL)) {
            if (rs.next()) {
                System.out.println("Verified inserted response data:");
                System.out.println("ID: " + rs.getInt("id") +
                                   ", MTI: " + rs.getString("f000") +
                                   ", Response Code: " + rs.getString("f039"));
            } else {
                System.out.println("No data found in the ISO8583_AUTH_RESPONSE table.");
            }
        }

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ISO8583_AUTH_RESPONSE")) {
            if (rs.next()) {
                System.out.println("Total number of responses: " + rs.getInt(1));
            }
        }
    }

    private static void verifyInsertion() throws SQLException {
        String verifySQL = "SELECT * FROM ISO8583_AUTH_REQUEST ORDER BY ID DESC LIMIT 1";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(verifySQL)) {
            if (rs.next()) {
                System.out.println("Verified inserted data:");
                // Print relevant data
            } else {
                System.out.println("No data found in the ISO8583_AUTH_REQUEST table.");
            }
        }

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM ISO8583_AUTH_REQUEST")) {
            if (rs.next()) {
                System.out.println("Total number of requests: " + rs.getInt(1));
            }
        }
    }
}