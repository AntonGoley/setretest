package ru.crystals.set10.pages.basic;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.crystals.set10.utils.DisinsectorTools;


public abstract class  AbstractPage {
	
	protected static final Logger log = Logger.getLogger(AbstractPage.class);
	public static final long DRIVER_WAIT_TIMEOUT = 15; 
	private WebDriver driver;
	private WebDriverWait wait;

	public AbstractPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
		setWebDriverWait(driver, DRIVER_WAIT_TIMEOUT);
	}
		
	public WebDriver getDriver() {
        return driver;
    }
	
	public WebDriverWait getWait() {
        return wait;
    }
	
	public void setWebDriverWait(WebDriver driver, long timeout) {
		wait = new WebDriverWait(driver, timeout);
	}
	
	public void isSWFReady() {
		getWait().until(ExpectedConditions.presenceOfElementLocated(By.id("isSWFReady")));
	}
	
	public void switchWindow(Boolean closeMainWindow) {

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
	
	public File exportFileData(String chromeDownloadPath, String reportNamePattern, SaveFile data, String fileType){
		if (DisinsectorTools.fileFilter(chromeDownloadPath, reportNamePattern).length != 0) {
			log.info(String.format("Предыдущие файлы отчетов %s не удалены перед выполнением теста", reportNamePattern));
			return new File("");
		}
		
		//clickElement(getDriver(), ID_OPERDAYSWF, LINK_SAVE_EXCEL);
		data.saveFile(fileType);
		
		return DisinsectorTools.getDownloadedFile(chromeDownloadPath, reportNamePattern);
	}
	
}
