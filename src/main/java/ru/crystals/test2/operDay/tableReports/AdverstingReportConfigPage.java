package ru.crystals.test2.operDay.tableReports;


import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static ru.crystals.test2.utils.FlexMediator.*;


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
		waitForElement(getDriver(), ID_OPERDAYSWF, "codeLabel");
	}
	
	@Override
	public HTMLRepotResultPage generateReport(String reportType){
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		switchWindow(false);
		return new HTMLRepotResultPage(getDriver());
	}
	
}
