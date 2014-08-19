package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  AbstractReportConfigPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	public static final String HTMLREPORT = "download_html";
	public static final String EXCELREPORT = "download_excel";
	
	
	public AbstractReportConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}

	public HTMLRepotResultPage generateReport(String reportType){
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		switchWindow(false);
		return new HTMLRepotResultPage(getDriver());
	}
	
	
//	// for excel and pdf reports
//	public void saveReportFile(){
//		// remove focus from 
//		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_ALERT_YES);
//		//doFlexMouseDown(getDriver(), ID_OPERDAYSWF, LOCATOR_ALERT_YES);
//		getDriver().findElement(By.id(ID_OPERDAYSWF)).click();
//		new Actions(getDriver()).sendKeys(Keys.SPACE).perform();
//		new Actions(getDriver()).sendKeys(Keys.SPACE).perform();
//	}
	
}
