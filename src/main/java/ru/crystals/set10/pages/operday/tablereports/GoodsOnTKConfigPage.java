package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  GoodsOnTKConfigPage extends ReportConfigPage{
	

	static final String LOCATOR_SETGOOD= "searchParam";
	public static final String LOCATOR_SUGGEST_CODE = "codeLabel";
	
	
	public GoodsOnTKConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public void setErpCode(String erpCode){
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, erpCode);
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SUGGEST_CODE);
		//выбрать элемент
		doFlexProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, new String[] {"selectedIndex", "0"});
	}
	
}
