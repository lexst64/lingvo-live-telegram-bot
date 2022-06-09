package com.lexst64.lingvolivetelegrambot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DBConnection {

    private final static String URL = "jdbc:sqlite:main.db";

    private final Connection currentConnection;

    public DBConnection() {
        currentConnection = openConnection();
    }

    private Connection openConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            currentConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean execute(String query) throws SQLException {
        return currentConnection.createStatement().execute(query);
    }

    public PreparedStatement getPreparedStatement(String query) throws SQLException {
        return currentConnection.prepareStatement(query);
    }

    public void initTransaction() throws SQLException {
        currentConnection.setAutoCommit(false);
    }

    /**
     * Finish a transaction in database and commit changes
     * @throws SQLException If a rollback fails
     */
    public void commitTransaction() throws SQLException {
        try {
            currentConnection.commit();
        } catch (SQLException e) {
            if (currentConnection != null) {
                currentConnection.rollback();
            }
        } finally {
            currentConnection.setAutoCommit(false);
        }
    }
}
