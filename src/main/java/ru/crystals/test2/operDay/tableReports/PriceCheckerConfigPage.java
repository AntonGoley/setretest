package ru.crystals.test2.operDay.tableReports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class  PriceCheckerConfigPage extends AbstractReportConfigPage{
	
	
	public PriceCheckerConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}

	public void setPeriod(){
	}
	
	
	
}
