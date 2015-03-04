package ru.crystals.set10.test;

import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeMethod;

import ru.crystals.set10.config.*;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.utils.CashEmulator;
import ru.crystals.set10.utils.DbAdapter;
import ru.crystals.set10.utils.DisinsectorTools;


@Listeners(ru.crystals.set10.test.AbstractTest.class)
public class  AbstractTest implements IExecutionListener{
	
    protected static final Logger log = Logger.getLogger(AbstractTest.class);
	
    private WebDriver driver;
    private static final int IMPLICIT_WAIT = 15; //sec
    private static ChromeDriverService service;
    protected static String chromeDownloadPath = null;

    protected static CashEmulator cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
    /*
     *  эмулятор для поиска чеков
     */
    protected static CashEmulator cashEmulatorSearchCheck = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER) + 1);
    /*
     * эмулятор для виртуального магазина
     */
    protected static CashEmulator cashEmulatorVirtualShop = CashEmulator.getCashEmulator(Config.CENTRUM_HOST, Integer.valueOf(Config.VIRTUAL_SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
   
    protected static DbAdapter dbAdapter = new DbAdapter();
    
    private static boolean firstRun = true;
    private static boolean serviceStatus = false;
    
    protected static String TARGET_HOST;
    protected static String TARGET_HOST_URL;
    protected static String TARGET_SHOP;
    
    
    
    
    public WebDriver getDriver() {
        return driver;
    }
    
    @BeforeClass (alwaysRun = true)
    public void setupWebDriver(ITestContext context) throws IOException {
	    
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("start-maximized");
    	
    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY,  options);
    	
    	driver = new RemoteWebDriver(service.getUrl(), capabilities);
    	
    	driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
    	driver.manage().deleteAllCookies();
    	
    	if (firstRun){
    		chromeDownloadPath = getChromeDownloadPath();
    		clearDownloadDir();
    	}	
    	
    	String[] groups = context.getIncludedGroups();
    	TARGET_HOST = Config.RETAIL_HOST;
    	TARGET_HOST_URL = Config.RETAIL_URL;
    	TARGET_SHOP = Config.SHOP_NUMBER;
    	for (int i=0; i<groups.length; i++){
    		if (groups[i].equals("centrum")){
    			TARGET_HOST = Config.CENTRUM_HOST;
    			TARGET_HOST_URL = Config.CENTRUM_URL;
    			TARGET_SHOP = Config.VIRTUAL_SHOP_NUMBER;
    		} 
    	}
	}
    
    @BeforeMethod(alwaysRun = true)
    public void printTestName(Method method) {
    	log.info("---------------------------------------");
    	log.info("TEST " + method.getDeclaringClass() + "." + method.getName() + " started.");

    	if (!method.getAnnotation(Test.class).description().equals("")) {
    		log.info("DESCRIPTION: " + method.getAnnotation(Test.class).description());
    	}	
    		log.info("---------------------------------------");
    }

    @AfterMethod(alwaysRun = true)
    public void printTestResult(Method method, ITestResult result) throws Exception {
    	log.info(method.getDeclaringClass() + "." +  method.getName() + " finished with RESULT " + result.getStatus());
//    	log.info("Asserts executed withing suite: " + method.getName() + " - " + Base.getAssertsEcexuted());
//    	log.info("TOTAL asserts executed: " + Base.getTotalAssertsEcexuted());
//    	Base.resetAssertsEcexuted();
    }
    
    @AfterClass (alwaysRun = true)
    public void close(ITestContext contx) {
    	driver.close();
    	//service.stop();  
    	// close all windows and quite
    	//driver.quit();
    }
    
    protected LoginPage loginAs(String user, String pwd, String url) {
    	return new LoginPage(getDriver(), url);
    }
    
    private String getChromeDownloadPath(){
    	if (chromeDownloadPath == null) {
    		setChromeDownloadPath();
    	}
    	return chromeDownloadPath;
    }
    
    public void setChromeDownloadPath() {
		driver.get("chrome://settings");
		driver.get("chrome://settings-frame");
		driver.findElement(By.xpath(".//a[@id='advanced-settings-expander']")).click();
		chromeDownloadPath = driver.findElement(By.xpath(".//input[@id='downloadLocationPath']")).getAttribute("value");
		log.info("Chrome download path: " + chromeDownloadPath);
	}
   
    /*
     * Удаление всех возможных старых файлов отчетов
     */
    private void clearDownloadDir(){
	    firstRun = false;
	    DisinsectorTools.removeOldReport(getChromeDownloadPath(), "*.xls");
	    DisinsectorTools.removeOldReport(getChromeDownloadPath(), "*.pdf");
	    DisinsectorTools.removeOldReport(getChromeDownloadPath(), "*.xlsx"); 
    }

	public synchronized void onExecutionStart(){
		if (!serviceStatus) {
			try {
				service = new ChromeDriverService.Builder()
			    .usingDriverExecutable(Config.DRIVER)
			    .usingAnyFreePort()
			    .build();
				log.info("Старт сервиса управления драйвером...");
				service.start();
				serviceStatus = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	};
	
	public synchronized void onExecutionFinish(){
		if (serviceStatus) {
			log.info("Остановка  сервиса управления драйвером...");
			service.stop();
			serviceStatus = false;
			log.info("Сервис успешно остановлен"); 
		}	
	};
    
}
