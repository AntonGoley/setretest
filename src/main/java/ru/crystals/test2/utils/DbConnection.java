package ru.crystals.test2.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConnection {
	
	public  Connection connection = null;
	
	public  void connectSet10Db(String host, String set10Db) {
		try {
			 
			connection = DriverManager.getConnection(
					String.format("jdbc:postgresql://%s:5432/%s", host, set10Db), "postgres",
					"postgres");
			
		} catch (SQLException e) {
 
			System.out.println("Connection Failed!");
			e.printStackTrace();
		}
	}
	
	public  void updateDb(String sqlUpdate){
		try {
			Statement statement = connection.createStatement();
			statement.execute(sqlUpdate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
