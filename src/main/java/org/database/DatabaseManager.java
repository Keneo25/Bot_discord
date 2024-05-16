package org.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

	private static final String DATABASE_URL = "jdbc:sqlite:database.sqlite";
	private Connection connection;

	static {
		try {
			// Load the SQLite JDBC driver (you must include the driver jar in your classpath)
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.err.println("SQLite JDBC driver not found. Make sure you have added the library to your project.");
			e.printStackTrace();
		}
	}

	public void connect() {
		try {
			connection = DriverManager.getConnection(DATABASE_URL);
			System.out.println("Connection to SQLite has been established.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException
	{
		if (connection.isClosed()){
			connect();
		}
		return connection;
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
