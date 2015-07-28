package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;

import static ru.crystals.set10.utils.FlexMediator.*;


public class  GoodsOnTKConfigPage extends ReportConfigPage{
	

	static final String LOCATOR_SETGOOD= "searchParam";
	public static final String LOCATOR_SUGGEST_CODE = "id:codeLabel;visible:true|0";
	
	
	public GoodsOnTKConfigPage(WebDriver driver) {
		super(driver);
	}
	
	public void setErpCode(String erpCode){
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, erpCode);
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SUGGEST_CODE);
		//выбрать элемент
		doFlexProperty(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, new String[] {"selectedIndex", "0"});
	}
	
}
