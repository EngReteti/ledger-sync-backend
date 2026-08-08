package com.ledger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:mariadb://localhost:3306/ledger_db";
    private static final String USER = "ledger_user";
    private static final String PASSWORD = "ledger_pass";

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        // Disable auto-commit to take manual control over transaction boundaries
        conn.setAutoCommit(false);
        return conn;
    }
}

