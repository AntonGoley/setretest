package ru.crystals.set10.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import ru.crystals.set10.config.*;


public class DbAdapter {
	
	public static final String DRIVER = "org.postgresql.Driver";
	private static String USERNAME = Config.DB_USER; 
	private static String PASSWORD = Config.DB_PASSWORD; 
	private static DriverManagerDataSource dataSource;
	private static JdbcTemplate template;
	private static HashMap<String, JdbcTemplate> connectionPool = new HashMap<String, JdbcTemplate>();
	
	public static final String DB_RETAIL_OPERDAY = String.format("jdbc:postgresql://%s:5432/%s", Config.RETAIL_HOST, "set_operday");
	
	public static final String DB_RETAIL_LOY = String.format("jdbc:postgresql://%s:5432/%s", Config.RETAIL_HOST, "set_loyal");
	
	public static final String DB_RETAIL_SET = String.format("jdbc:postgresql://%s:5432/%s", Config.RETAIL_HOST, "set");
	public static final String DB_CENTRUM_OPERDAY = String.format("jdbc:postgresql://%s:5432/%s", Config.CENTRUM_HOST, "set_operday"); 
	public static final String DB_CENTRUM_SET = String.format("jdbc:postgresql://%s:5432/%s", Config.CENTRUM_HOST, "set");
	
	
	static
	{	
		setConnectionPool(new String[] {DB_RETAIL_OPERDAY, DB_CENTRUM_SET, DB_RETAIL_SET, DB_CENTRUM_OPERDAY, DB_RETAIL_LOY});
	}
	
	
	private static void  setConnectionPool(String[] dbList){
		String dbUrl;
		for (int i=0; i < dbList.length; i++) {
			dbUrl = dbList[i];
			dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName(DRIVER);
			dataSource.setUrl(dbUrl);
			dataSource.setUsername(USERNAME);
			dataSource.setPassword(PASSWORD);
			template = new JdbcTemplate(dataSource);
			connectionPool.put(dbUrl, template);
		}
	}
	
	public  int queryForInt(String db, String sql) {
		return connectionPool.get(db).queryForInt(sql); 
	}
	
	@SuppressWarnings("unchecked")
	public  List<Map<String, String>> queryForList(String db, String sql) {
		return (ArrayList<Map<String, String>>) connectionPool.get(db).queryForList(sql); 
	}
	
	public  SqlRowSet queryForRowSet(String db, String sql) {
		return connectionPool.get(db).queryForRowSet(sql); 
	}
	

	public  String queryForString(String db, String sql) {
		String result = "";
		result = (String) connectionPool.get(db).queryForObject(sql, result.getClass());
		return result;
	}
	
	public void updateDb(String db, String sql){
		connectionPool.get(db).update(sql);	
	}
	
	public void batchUpdateDb(String db, String[] sql){
		connectionPool.get(db).batchUpdate(sql);	
	}
}


