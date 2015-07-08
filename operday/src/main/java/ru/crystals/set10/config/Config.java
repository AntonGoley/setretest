package ru.crystals.set10.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import ru.crystals.set10.utils.DisinsectorTools;




public class Config {
	
	private final static org.slf4j.Logger log =  LoggerFactory.getLogger(Config.class);

	public static String CENTRUM_URL;
    public static  String RETAIL_URL;
    public static  File  DRIVER;
    public static  String CENTRUM_HOST;
    public static  String RETAIL_HOST;
    public static  String SHOP_NUMBER;
    public static  String DEFAULT_PORT;
    public static  String DB_USER;
    public static  String DB_PASSWORD;
    public static  String CHECK_COUNT;
    public static String NEXT_SHIFT;
    public static String VIRTUAL_WEIGHT_PATH;
    
    /*
     * Настройки весового модуля
     */
    public static String WEIGHT_BARCODE_PREFIX;
    public static String WEIGHT_BARCODEGENERATION_PREFIX;
    public static String WEIGHT_BARCODEGENERATION_OFSET;
    
    /*
     * Кассы
     */
    public static String CASH_NUMBER;
    public static String CASH_NUMBER_SEARCH;
    public static String CASH_NUMBER_OPERDAY;
    
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
    
    
    // Управление продажами - Общие настройки
    public static int SALES_PREFERENCES_INDEX;
    
    private static Properties props;
    
    private static ClassLoader classLoader = Config.class.getClassLoader();
    
    static {
        try (
        		InputStream i = classLoader.getResourceAsStream("testing.properties");
        		// TODO: разрулить запуск с разных ос и разных драйверов
        		InputStream iDriver = classLoader.getResourceAsStream("drivers/chromedriver_win.exe");
        		OutputStream outDriver = new FileOutputStream(new File("driver"));
        	)
       {	
        	
        	props = new Properties();
        	props.load(i);
        	
        	int read = 0;
    		byte[] bytes = new byte[1024];
     
    		while ((read = iDriver.read(bytes)) != -1) {
    			outDriver.write(bytes, 0, read);
    		} 
    		DRIVER = new File("driver");
        	
    		iDriver.close();
    		outDriver.close();
            i.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.setProperty("webdriver.chrome.logfile", "chromedriver.log");
        
        // Берем параметры из коммандной строки (передаваемые при запуске проекта maven)
        CENTRUM_HOST = System.getProperty("test_centrum_host");
        RETAIL_HOST = System.getProperty("test_retail_host");
        SHOP_NUMBER = System.getProperty("test_shop_number");
        VIRTUAL_SHOP_NUMBER = System.getProperty("test_virtualshop_number");
    	DB_USER = System.getProperty("test_dbUser");
    	DB_PASSWORD = System.getProperty("test_dbPassword");
    	CASH_NUMBER = System.getProperty("test_cash_number");
    	CHECK_COUNT = System.getProperty("test_check_number");
    	NEXT_SHIFT = System.getProperty("test_next_shift_number");
    	
        // если какие-то параметры не были переданы в коммандной строке, берем значения из проперти файла
    	CENTRUM_HOST = StringUtils.defaultString(CENTRUM_HOST, props.getProperty("centrum.host.ip"));
    	RETAIL_HOST = StringUtils.defaultString(RETAIL_HOST, props.getProperty("retail.host.ip"));
    	SHOP_NUMBER = StringUtils.defaultString(SHOP_NUMBER, props.getProperty("shop.number"));
    	VIRTUAL_SHOP_NUMBER = StringUtils.defaultString(VIRTUAL_SHOP_NUMBER, props.getProperty("virtual.shop.number"));
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
        
        /*
         * Номера основных касс
         */
        CASH_NUMBER_SEARCH = String.valueOf(Integer.valueOf(CASH_NUMBER) + 1);
        CASH_NUMBER_OPERDAY = String.valueOf(Integer.valueOf(CASH_NUMBER) + 2);
        
        log.info("Centrum url:   " + CENTRUM_URL);
        log.info("Retail url:   " + RETAIL_URL);
        log.info("Shop number :   " + SHOP_NUMBER);
        log.info("Virtual shop number :   " + VIRTUAL_SHOP_NUMBER);
        log.info("Manager login:   " + MANAGER);
        log.info("Manager password:   " + MANAGER_PASSWORD); 
        log.info("Base port:   " + DEFAULT_PORT);
        log.info("DB username:   " + DB_USER);
        log.info("DB password:   " + DB_PASSWORD);
        log.info("Cash number:   " + CASH_NUMBER);
        
        DisinsectorTools.delay(10000);
        
        /*
         * Виртуальные весы
         */
        VIRTUAL_WEIGHT_PATH = "http://" + RETAIL_HOST + ":" + props.getProperty("nginx.port") + props.getProperty("virtualweight.path");
        WEIGHT_BARCODE_PREFIX = props.getProperty("weight.barcodeprefix");
        WEIGHT_BARCODEGENERATION_PREFIX = props.getProperty("weight.barcodegeneration.prefix");
        WEIGHT_BARCODEGENERATION_OFSET = props.getProperty("weight.barcodegeneration.ofset");
        log.info("Path to virtual scales:   " + VIRTUAL_WEIGHT_PATH);
        log.info("Weight prefix:   " + WEIGHT_BARCODE_PREFIX);
        log.info("Weight barcode autogeneration prefix:   " + WEIGHT_BARCODEGENERATION_PREFIX);
        log.info("Weight barcode autogeneration ofset:   " + WEIGHT_BARCODEGENERATION_OFSET);
    }
    
}
