package ru.crystals.set10.utils;

//import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.crystals.set10.config.*;


public class DbAdapter {
	
	private final static org.slf4j.Logger log =  LoggerFactory.getLogger(DbAdapter.class);
	public static final String DRIVER = "org.postgresql.Driver";
	private static String USERNAME = Config.DB_USER; 
	private static String PASSWORD = Config.DB_PASSWORD; 
	private static DriverManagerDataSource dataSource;
	private static JdbcTemplate template;
	private static HashMap<String, JdbcTemplate> connectionPool = new HashMap<String, JdbcTemplate>();
	
	public static final String DB_RETAIL_OPERDAY = String.format("jdbc:postgresql://%s:5432/%s", Config.RETAIL_HOST, "set_operday"); 
	public static final String DB_CENTRUM_SET = String.format("jdbc:postgresql://%s:5432/%s", Config.CENTRUM_HOST, "set");
	public static final String DB_RETAIL_SET = String.format("jdbc:postgresql://%s:5432/%s", Config.RETAIL_HOST, "set");
	
	{	
		setConnectionPool(new String[] {DB_RETAIL_OPERDAY, DB_CENTRUM_SET, DB_RETAIL_SET});
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

	public  String queryForString(String db, String sql) {
		String result = "";
		result = (String) connectionPool.get(db).queryForObject(sql, result.getClass());
		return result;
	}
	
	public void updateDb(String db, String sql){
		connectionPool.get(db).update(sql);	
	}
}


