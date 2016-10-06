package com.NetworkAnalysis.rsc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionUtil {

    private DataSource dataSource;

    private static ConnectionUtil instance = new ConnectionUtil();

    private ConnectionUtil() {
        try {
            Context initContext = new InitialContext();
            dataSource = (DataSource) initContext.lookup("JNDI_LOOKUP_NAME");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionUtil getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        return connection;
    }

    public void close(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connection = null;
    }

}