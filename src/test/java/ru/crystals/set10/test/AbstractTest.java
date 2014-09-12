package ru.crystals.set10.test;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
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


public class AbstractTest {
	
    protected static final Logger log = Logger.getLogger(AbstractTest.class);
	
    private WebDriver driver;
    private static ChromeDriverService service;
    private static String chromeDownloadPath = null;
    
    public WebDriver getDriver() {
        return driver;
    }
    
    @BeforeClass (alwaysRun = true)
    public void setupWebDriver() throws IOException {
	    
    	service = new ChromeDriverService.Builder()
         .usingDriverExecutable(new File(Config.PATH_TO_DRIVER))
         .usingAnyFreePort()
         .build();
    	service.start();

    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("start-maximized");
    	//options.addArguments("--always-authorize-plugins");
    	//options.addArguments("--enable-extensions");

    	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    	capabilities.setCapability(ChromeOptions.CAPABILITY,  options);
    	
    	driver = new RemoteWebDriver(service.getUrl(), capabilities);
    	
    	driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    	driver.manage().deleteAllCookies();
    	
    	chromeDownloadPath =getChromeDownloadPath();
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
    	// close all windows and quite
    	driver.quit();
    	log.info("trying to stop service");
    	service.stop();
    	log.info("service has stopped successfully");
    }
    
    protected LoginPage loginAs(String user, String pwd, String url) {
    	return new LoginPage(getDriver(), url);
    	
    }
    
    protected String getChromeDownloadPath(){
    	if (chromeDownloadPath == null) {
    		setChromeDownloadPath();
    	}
    	return this.chromeDownloadPath;
    }
    
    public String setChromeDownloadPath() {
		driver.get("chrome://settings");
		driver.get("chrome://settings-frame");
		driver.findElement(By.xpath(".//button[@id='advanced-settings-expander']")).click();
		return driver.findElement(By.xpath(".//input[@id='downloadLocationPath']")).getAttribute("value");
	}
    
}
