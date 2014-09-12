package ru.crystals.set10.config;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
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
    public static String NEXT_SHIFT;
    
    private static Properties props;

    
    static {
        try (
        	Reader reader = new FileReader("target/classes/testing.properties");
        	)
        {	
        	props = new Properties();
        	props.load(reader);
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // TODO: разрулить запуск с разных ос
        PATH_TO_DRIVER = "target/test-classes/chromedriver.exe";
        //PATH_TO_DRIVER = "target/test-classes/chromedriver 2";
        
        
        // Берем параметры из коммандной строки (передаваемые при запуске проекта maven)
        CENTRUM_HOST = System.getProperty("testng_centrum_host");
        RETAIL_HOST = System.getProperty("testng_retail_host");
        SHOP_NUMBER = System.getProperty("testng_shop_number");
    	DB_USER = System.getProperty("testng_dbUser");
    	DB_PASSWORD = System.getProperty("testng_dbPassword");
    	CASH_NUMBER = System.getProperty("testng_cash_number");
    	CHECK_COUNT = System.getProperty("testng_check_number");
    	NEXT_SHIFT = System.getProperty("testng_next_shift_number");
    	
        // если какие-то параметры не были переданы в коммандной строке, берем значения из проперти файла
    	CENTRUM_HOST = StringUtils.defaultString(CENTRUM_HOST, props.getProperty("centrum.host.ip"));
    	RETAIL_HOST = StringUtils.defaultString(RETAIL_HOST, props.getProperty("retail.host.ip"));
    	SHOP_NUMBER = StringUtils.defaultString(SHOP_NUMBER, props.getProperty("shop.number"));
    	DB_USER = StringUtils.defaultString(DB_USER, props.getProperty("db_user"));
    	DB_PASSWORD = StringUtils.defaultString(DB_PASSWORD, props.getProperty("db_password"));
    	CASH_NUMBER = StringUtils.defaultString(CASH_NUMBER, props.getProperty("cash.number"));
    	CHECK_COUNT = StringUtils.defaultString(CHECK_COUNT, props.getProperty("check.count"));
    	NEXT_SHIFT = StringUtils.defaultString(NEXT_SHIFT, "false");
    	
        // параметры, которые хранятся только в проперти файле
        MANAGER =  props.getProperty("managerLogin");
        MANAGER_PASSWORD =  props.getProperty("managerPassword");
        DEFAULT_PORT = props.getProperty("port");
        
        CENTRUM_URL = "http://" + CENTRUM_HOST + ":" + props.getProperty("port");
        RETAIL_URL = "http://" + RETAIL_HOST + ":" + props.getProperty("port");
        
        
        log.info("Centrum url:   " + CENTRUM_URL);
        log.info("Retail url:   " + RETAIL_URL);
        log.info("Manager login:   " + MANAGER);
        log.info("Manager password:   " + MANAGER_PASSWORD); 
        log.info("Base port:   " + DEFAULT_PORT);
        log.info("DB username:   " + DB_USER);
        log.info("DB password:   " + DB_PASSWORD);
    }
    
}
