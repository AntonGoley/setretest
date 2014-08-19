package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  AdverstingReportConfigPage extends AbstractReportConfigPage{
	
	static final String LOCATOR_SETERPCODE = "goodSearchWidget";
	static final String LOCATOR_SETSHOP= "id:shopSearchWidget/id:searchTextBox";
	static final String LOCATOR_SETGOOD= "id:goodSearchWidget/id:searchTextBox";
	public static final String LOCATOR_ALERT_YES = "title=YES";
	public static final String LOCATOR_SUGGEST_CODE = "codeLabel";
	
	
	public AdverstingReportConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_OPERDAYSWF)));
	}
	
	public void setShopNumber(String shopNumber){
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SETSHOP);
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETSHOP, shopNumber);
	}
	
	public void setGoodIDs(String goodIDs){
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD);
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, goodIDs);
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SUGGEST_CODE);
	}
	
}
