package ru.crystals.set10.pages.basic;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.crystals.set10.utils.DisinsectorTools;


public abstract class  AbstractPage {
	
	protected static final Logger log = Logger.getLogger(AbstractPage.class);
	public static final long DRIVER_WAIT_TIMEOUT = 15; 
	private WebDriver driver;
	private WebDriverWait wait;
	
	
	//public static final String BASEURL = Config.BASE_URL; 

	public AbstractPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		setWebDriverWaitTimeOut(driver, DRIVER_WAIT_TIMEOUT);
	}
		
	public WebDriver getDriver() {
        return driver;
    }
	
	public WebDriverWait getWait() {
        return wait;
    }
	
	public void setWebDriverWaitTimeOut(WebDriver driver, long timeout) {
		wait = new WebDriverWait(driver, timeout);
	}
	
	public void isSWFReady() {
		getWait().until(ExpectedConditions.presenceOfElementLocated(By.id("isSWFReady")));
	}
	
	
	public boolean waitPropertyValue(String flexId, String property, String propertyExpectedValue) {
		int timeout = 10;
		int counter = 0;
		String propertyActualValue;
		JavascriptExecutor js = (JavascriptExecutor) getDriver(); 
		while (counter<timeout*1000) {
				propertyActualValue = (String)js.executeScript("return document.getElementById('RetailX').getFlexProperty(arguments[0], arguments[1])", flexId, property);
			
			if (propertyExpectedValue.equals(propertyActualValue)) {
				return true;
			}
			counter+=1000;
		}
		return false;
	}
	
	
	public void switchWindow(Boolean closeMainWindow) {
		// get main window name
		
		DisinsectorTools.delay(2000);
		String mainWindow = getDriver().getWindowHandle();
		Set<String> set = getDriver().getWindowHandles();
		if (closeMainWindow) {
			getDriver().close();
		}
		
		Iterator<String> i = set.iterator();
		String window;
		while (i.hasNext()) {
		    if(!(window = i.next()).equals(mainWindow))
		    {
		         getDriver().switchTo().window(window);
		         getDriver().manage().window().maximize();
		    }
		}
	}
	
	public boolean isElementDisplayedSingleP(WebElement element) {
		try {
        	getWait().until(ExpectedConditions.visibilityOf(element));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }
	
	public void delay(long delayTimeout){
		try {
			Thread.sleep(delayTimeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
