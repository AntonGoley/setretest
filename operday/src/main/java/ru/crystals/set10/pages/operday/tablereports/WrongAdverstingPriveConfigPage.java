package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class  WrongAdverstingPriveConfigPage extends ReportConfigPage{
	
	
	public WrongAdverstingPriveConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}

	public void setPeriod(){
		
	}
	
	
	
}
