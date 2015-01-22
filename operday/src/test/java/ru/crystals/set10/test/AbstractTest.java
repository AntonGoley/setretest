package ru.crystals.set10.test;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import java.io.File;
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


public class AbstractTest {
	
    protected static final Logger log = Logger.getLogger(AbstractTest.class);
	
    private WebDriver driver;
    private static final int IMPLICIT_WAIT = 15; //sec
    private static ChromeDriverService service;
    protected static String chromeDownloadPath = null;
    protected static CashEmulator cashEmulator = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
    // эмулятор для поиска чеков
    protected static CashEmulator cashEmulatorSearchCheck = CashEmulator.getCashEmulator(Config.RETAIL_HOST, Integer.valueOf(Config.SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER) + 1);
    protected static CashEmulator cashEmulatorVirtualShop = CashEmulator.getCashEmulator(Config.CENTRUM_HOST, Integer.valueOf(Config.VIRTUAL_SHOP_NUMBER), Integer.valueOf(Config.CASH_NUMBER));
    protected static DbAdapter dbAdapter = new DbAdapter();
    
    public WebDriver getDriver() {
        return driver;
    }
    
    
    @BeforeSuite
    public void setService() throws IOException {
    	
    	service = new ChromeDriverService.Builder()
        .usingDriverExecutable(new File(Config.PATH_TO_DRIVER))
        .usingAnyFreePort()
        .build();
    	service.start();
    }
    
    @BeforeClass (alwaysRun = true)
    public void setupWebDriver() throws IOException {
	    
    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("start-maximized");
    	//options.addArguments("--always-authorize-plugins");
    	//options.addArguments("--enable-extensions");
    	
    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY,  options);
    	
    	driver = new RemoteWebDriver(service.getUrl(), capabilities);
    	
    	driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT, TimeUnit.SECONDS);
    	driver.manage().deleteAllCookies();
    	chromeDownloadPath = getChromeDownloadPath();
    	
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
    public void  closeBrowser(){
    	log.info("trying to stop service");
    	service.stop();
    	log.info("service has stopped successfully");
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
		new DisinsectorTools().removeOldDownloadedReports(getChromeDownloadPath());
	}
}
