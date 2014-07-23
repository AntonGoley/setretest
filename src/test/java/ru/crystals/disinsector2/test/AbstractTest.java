package ru.crystals.disinsector2.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeMethod;
import ru.crystals.test2.config.*;
import ru.crystals.test2.utils.DbConnection;
import ru.crystals.test2.utils.TestConfiguration;
import ru.crystals.test2.basic.*;
import ru.crystals.test2.basic.LoginPage.Set10ShopRoles;

public class AbstractTest {
	
    protected static final Logger log = Logger.getLogger(AbstractTest.class);
	
    private WebDriver driver;
    private static ChromeDriverService service;
    
    TestConfiguration configuration;

    public WebDriver getDriver() {
        return driver;
    }
    
    @BeforeClass
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
    	//TODO: как устанавливать роль польлзователю?
    	
//    	configuration = new TestConfiguration(driver);
//    	if (!TestConfiguration.shopAdministratorConfigured) 
//    		configuration.setRole(Config.MANAGER);
//    	
//    	if (!TestConfiguration.shopConfigured) 
//    		configuration.addShop();
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

//    @AfterMethod(alwaysRun = true)
//    public void printTestResult(Method method, ITestResult result) throws Exception {
//    	log.info(method.getDeclaringClass() + "." +  method.getName() + " finished with RESULT " + result.getStatus());
//    	log.info("Asserts executed withing suite: " + method.getName() + " - " + Base.getAssertsEcexuted());
//    	log.info("TOTAL asserts executed: " + Base.getTotalAssertsEcexuted());
//    	Base.resetAssertsEcexuted();
//    }
    
    @AfterClass
    public void close() {
    	driver.close();
    	// close all windows and quite
    	driver.quit();
    	log.info("trying to stop service");
    	service.stop();
    	log.info("service has stopped successfully");
    }
    
    protected LoginPage loginAs(String user, String pwd) {
    	getDriver().get(AbstractPage.BASEURL);
    	return new LoginPage(getDriver());
    	
    }
    
   
    
}
