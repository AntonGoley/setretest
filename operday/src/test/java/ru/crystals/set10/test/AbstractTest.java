package ru.crystals.set10.test;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
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


public class  AbstractTest{
	
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
    private static int suiteFiles = 0;
    
    static {
    	service = new ChromeDriverService.Builder()
        .usingDriverExecutable(Config.DRIVER)
        .usingAnyFreePort()
        .build();
    	try {
    		log.info("Старт сервиса управления драйвером...");
			service.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
    
    public WebDriver getDriver() {
        return driver;
    }
    
    @BeforeSuite
    public synchronized void setService() throws IOException {
    	suiteFiles++;
    	log.info("Запущено сьютов: "  + suiteFiles);
    }
    
    @BeforeClass (alwaysRun = true)
    public void setupWebDriver() throws IOException {
	    
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
    public void close() {
    	driver.close();
    	//service.stop();  
    	// close all windows and quite
    	//driver.quit();
    }
    
    @AfterSuite
    public synchronized void  closeBrowser(ITestContext context){
    	suiteFiles--; 
    	log.info("Сьютов в процессе выполнения: "  + suiteFiles);
    	log.info("Выполнение сьюта " + context.getSuite().getName().toUpperCase() + " завершено");
    	if(suiteFiles == 0) {
    		log.info("Остановка  сервиса управления драйвером...");
    		service.stop();
    		log.info("Сервис успешно остановлен");
    	}	
    	
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
    
}
