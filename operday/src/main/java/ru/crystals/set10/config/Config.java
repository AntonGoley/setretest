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
    public static  String CENTRUM_HOST;
    public static  String RETAIL_HOST;
    public static  String SHOP_NUMBER;
    public static  String DEFAULT_PORT;
    public static  String DB_USER;
    public static  String DB_PASSWORD;
    public static  String CHECK_COUNT;
    public static String CASH_NUMBER;
    public static String NEXT_SHIFT;
    public static String VIRTUAL_WEIGHT_PATH;
    public static String WEIGHT_BARCODE_PREFIX;
    
    /*
     * Настройки юридического лица для магазина
     */
    public static String SHOP_NAME;
    public static String SHOP_ADRESS;
    public static String SHOP_PHONE;
    public static String SHOP_INN;
    public static String SHOP_KPP;
    public static String SHOP_OKPO;
    public static String SHOP_OKDP;
    
    /*
     * Настройки юридического виртуального магазина
     */
    public static  String VIRTUAL_SHOP_NUMBER;
    public static String VIRTUAL_SHOP_NAME;
    public static String VIRTUAL_SHOP_ADRESS;
    public static String VIRTUAL_SHOP_PHONE;
    public static String VIRTUAL_SHOP_INN;
    public static String VIRTUAL_SHOP_KPP;
    public static String VIRTUAL_SHOP_OKPO;
    public static String VIRTUAL_SHOP_OKDP;
    
    /*
     * Пользователь manager
     */
    public static  String MANAGER;
    public static  String MANAGER_PASSWORD;
    public static  String MANAGER_NAME;
    public static  String MANAGER_LASTNAME;
    public static  String MANAGER_MIDDLENAME;
    
    /*
     * Кассир администратор
     */
    
    public static  String CASHIER_ADMIN_NAME;
    public static  String CASHIER_ADMIN_MIDDLE_NAME;
    public static  String CASHIER_ADMIN_LAST_NAME;
    public static  String CASHIER_ADMIN_TAB_NUM;
    public static  String CASHIER_ADMIN_PASSWORD;
    public static  String CASHIER_ADMIN_ROLE;
    
    /*
     * Внешние системы
     */
    public static  String BANK_NAME_1;
    public static  String BANK_NAME_2;
    
    private static Properties props;

    
    static {
        try (
        	Reader reader = new FileReader("target/test-classes/testing.properties");
        	)
        {	
        	props = new Properties();
        	props.load(reader);
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // TODO: разрулить запуск с разных ос
        PATH_TO_DRIVER = "target/classes/chromedriver_win.exe";
//        PATH_TO_DRIVER = "target/test-classes/chromedriver";
        
        
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
    	
        /*
         * параметры, которые хранятся только в проперти файле  testng.properties
         */

    	/*
    	 *  Пользователь manager
    	 */
        MANAGER =  props.getProperty("manager.login");
        MANAGER_PASSWORD =  props.getProperty("manager.password");
        MANAGER_NAME =  props.getProperty("manager.name");
        MANAGER_LASTNAME =  props.getProperty("manager.lastname");
        MANAGER_MIDDLENAME =  props.getProperty("manager.middlename");
        
        /*
         * Кассир
         */
        CASHIER_ADMIN_NAME =  props.getProperty("cashier.admin.name");
        CASHIER_ADMIN_MIDDLE_NAME =  props.getProperty("cashier.admin.middleName");
        CASHIER_ADMIN_LAST_NAME =  props.getProperty("cashier.admin.surnameName");
        CASHIER_ADMIN_TAB_NUM =  props.getProperty("cashier.admin.tabnum");
        CASHIER_ADMIN_PASSWORD =  props.getProperty("cashier.admin.password");
        CASHIER_ADMIN_ROLE = "Администратор";
        /*
         * Магазин
         */
        SHOP_NAME = "Magazin " + SHOP_NUMBER;
        SHOP_ADRESS = props.getProperty("shop.adress");
        SHOP_PHONE = props.getProperty("shop.phone");
        SHOP_INN = props.getProperty("shop.inn");
        SHOP_KPP = props.getProperty("shop.kpp");
        SHOP_OKPO = props.getProperty("shop.okpo");
        SHOP_OKDP = props.getProperty("shop.okdp");
        
        DEFAULT_PORT = props.getProperty("port");
        CENTRUM_URL = "http://" + CENTRUM_HOST + ":" + props.getProperty("port");
        RETAIL_URL = "http://" + RETAIL_HOST + ":" + props.getProperty("port");
        
        /*
         * Виртуальный Магазин
         */
        VIRTUAL_SHOP_NUMBER = props.getProperty("virtual.shop.number");
        VIRTUAL_SHOP_NAME = "VirualMagazin " + VIRTUAL_SHOP_NUMBER;
        VIRTUAL_SHOP_ADRESS = props.getProperty("virtual.shop.adress");
        VIRTUAL_SHOP_PHONE = props.getProperty("virtual.shop.phone");
        VIRTUAL_SHOP_INN = props.getProperty("virtual.shop.inn");
        VIRTUAL_SHOP_KPP = props.getProperty("virtual.shop.kpp");
        VIRTUAL_SHOP_OKPO = props.getProperty("virtual.shop.okpo");
        VIRTUAL_SHOP_OKDP = props.getProperty("virtual.shop.okdp");
        
        /*
         * Внешние системы
         */
        BANK_NAME_1 = props.getProperty("externalsystem.bank.name1"); 
        BANK_NAME_2 = props.getProperty("externalsystem.bank.name2");
        
        log.info("Centrum url:   " + CENTRUM_URL);
        log.info("Retail url:   " + RETAIL_URL);
        log.info("Manager login:   " + MANAGER);
        log.info("Manager password:   " + MANAGER_PASSWORD); 
        log.info("Base port:   " + DEFAULT_PORT);
        log.info("DB username:   " + DB_USER);
        log.info("DB password:   " + DB_PASSWORD);
        
        /*
         * Виртуальные весы
         */
        VIRTUAL_WEIGHT_PATH = "http://" + RETAIL_HOST + ":" + props.getProperty("nginx.port") + props.getProperty("virtualweight.path");
        WEIGHT_BARCODE_PREFIX = props.getProperty("weight.barcodeprefix");
        log.info("Path to virtual scales:   " + VIRTUAL_WEIGHT_PATH);
    }
    
}
