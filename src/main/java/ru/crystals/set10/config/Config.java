package ru.crystals.set10.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import org.slf4j.LoggerFactory;


public class Config {
	
	private final static org.slf4j.Logger log =  LoggerFactory.getLogger(Config.class);

    public static  String CENTRUM_URL;
    public static  String RETAIL_URL;
    public static  String PATH_TO_DRIVER;
    public static  String MANAGER;
    public static  String MANAGER_PASSWORD;
    public static  String CENTRUM_HOST;
    public static  String RETAIL_HOST;
    public static  String SHOP_NUMBER;
    public static  String DEFAULT_PORT;
    public static  String DB_USER;
    public static  String DB_PASSWORD;
    public static  String CHECK_COUNT;
    public static String CASH_NUMBER;
    public static final boolean NEXT_SHIFT;
    
    private static Properties props;

    
    static {
        try (
        	//TODO: добавить возможность запуска с разными конфигурациями	
        	Reader reader = new FileReader("target/classes/testing.properties");
        	)
        {	
        	props = new Properties();
        	props.load(reader);
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        CENTRUM_HOST = System.getProperty("testng_centrum_host");
        RETAIL_HOST = System.getProperty("testng_retail_host");
        SHOP_NUMBER = System.getProperty("testng_shop_number");
    	CHECK_COUNT = System.getProperty("testng_check_number");
    	CHECK_COUNT = "2";
    	DB_USER = System.getProperty("testng_dbUser");
    	DB_PASSWORD = System.getProperty("testng_dbPassword");
    	CASH_NUMBER = System.getProperty("testng_cash_number");
    	if (CASH_NUMBER == null) CASH_NUMBER = "1";
    	
    	NEXT_SHIFT = Boolean.valueOf(System.getProperty("testng_next_shift_number"));
    	
        
        PATH_TO_DRIVER = "target/test-classes/chromedriver.exe";
        
        //TODO: сделать обработчик параметров
        CENTRUM_HOST= props.getProperty("centrum.host.ip");
        RETAIL_HOST = props.getProperty("retail.host.ip");
        SHOP_NUMBER = props.getProperty("shop.number");
        DB_USER = props.getProperty("db_user");
        DB_PASSWORD = props.getProperty("db_password");
        
        CENTRUM_URL = "http://" + CENTRUM_HOST + ":" + props.getProperty("port");
        RETAIL_URL = "http://" + RETAIL_HOST + ":" + props.getProperty("port");
        MANAGER =  props.getProperty("managerLogin");
        MANAGER_PASSWORD =  props.getProperty("managerPassword");
        DEFAULT_PORT = props.getProperty("port");
        
        
        
        log.info("Centrum url:   " + CENTRUM_URL);
        log.info("Centrum url:   " + RETAIL_URL);
        log.info("Manager login:   " + MANAGER);
        log.info("Manager password:   " + MANAGER_PASSWORD); 
        log.info("Centrum host:   " + CENTRUM_HOST);
        log.info("Retail host:   " + RETAIL_HOST);
        log.info("Base port:   " + DEFAULT_PORT);
        log.info("DB username:   " + DB_USER);
        log.info("DB password:   " + DB_PASSWORD);
    }
    
}
