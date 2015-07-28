package ru.crystals.set10.pages.operday.tablereports;


import org.openqa.selenium.*;
import static ru.crystals.set10.utils.FlexMediator.*;


public class  AdverstingReportConfigPage extends ReportConfigPage{
	
	static final String LOCATOR_SETERPCODE = "goodSearchWidget";
	static final String LOCATOR_SETSHOP= "id:shopSearchWidget/id:searchTextBox";
	static final String LOCATOR_SETGOOD= "id:goodSearchWidget/id:searchTextBox";
	public static final String LOCATOR_ALERT_YES = "title=YES";
	public static final String LOCATOR_SUGGEST_CODE = "id:codeLabel;visible:true|0";
	
	
	public AdverstingReportConfigPage(WebDriver driver) {
		super(driver);
	}
	
	public void setShopNumber(String shopNumber){
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETSHOP, shopNumber);
	}
	
	public void setGoodIDs(String goodIDs){
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, goodIDs);
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SUGGEST_CODE);
		log.info("Код товара для просмотра рекламных акций:" + goodIDs);
	}
	
	public void clearGoodField(){
		typeText(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD, "");
	}
	
}
