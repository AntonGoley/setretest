package ru.crystals.set10.pages.basic;

import static ru.crystals.set10.utils.FlexMediator.waitForProperty;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.parser.PdfTextExtractor;

import ru.crystals.set10.utils.DisinsectorTools;


public abstract class  AbstractPage {
	
	protected static final Logger log = Logger.getLogger(AbstractPage.class);
	public static final long DRIVER_WAIT_TIMEOUT = 25; 
	private WebDriver driver;
	private WebDriverWait wait;
	protected static final String SPINNER = "id:loadingMask";

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
	
	public void waitSpinner(String ID_SWF){
		log.info("Ожидание спиннера...");
		/* удалить проверку, когда перенесут новый спиннер в Sales */
		if (ID_SWF.equals(SalesPage.ID_SALESSWF)) {
			DisinsectorTools.delay(1500);
			return;
		}
		waitForProperty(getDriver(), ID_SWF, SPINNER, new String[]{"visible", "false"});
	}
	
	public void switchWindow(Boolean closeMainWindow) {
		
		Set<String> set = getDriver().getWindowHandles();
		/*
		 * Если вызывается метод, то ожидается 
		 *  больше, чем одно окно
		 */
		long timeout = 0;
		while (timeout < 10000) {
			if (set.size() > 1) break;
			timeout+=100;
			set = getDriver().getWindowHandles();
		}
		
		String mainWindow = getDriver().getWindowHandle();
		
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
	
	public File exportFileData(String chromeDownloadPath, String reportNamePattern, SaveFile save, String fileType){
		if (DisinsectorTools.fileFilter(chromeDownloadPath, reportNamePattern).length != 0) {
			log.info(String.format("Предыдущие файлы отчетов %s не удалены перед выполнением теста", reportNamePattern));
			return new File("");
		}
		save.saveFile(fileType);
		return DisinsectorTools.getDownloadedFile(chromeDownloadPath, reportNamePattern);
	}
	
	
	public String getPDFFilePageContent(File file, int pageNumber){
		String result = "";
		PdfReader reader;
		try {
			reader = new PdfReader(file.getAbsolutePath());
			PdfTextExtractor parser = new PdfTextExtractor(reader);
			result = parser.getTextFromPage(pageNumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(result);
		return result;
	}
	
	public String getPDFFileContent(File file){
		String result = "";
		PdfReader reader;
		try {
			reader = new PdfReader(file.getAbsolutePath());
			PdfTextExtractor parser = new PdfTextExtractor(reader);

			for (int i=1; i<=reader.getNumberOfPages(); i++){
				result += parser.getTextFromPage(i);
			};
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(result);
		return result;
	}
	
	public String getFlexAlertMessage(){
		return "";
	}
	
	public AbstractPage closeAlertMessage(Boolean desicion){
		return this;
	}
	
}
