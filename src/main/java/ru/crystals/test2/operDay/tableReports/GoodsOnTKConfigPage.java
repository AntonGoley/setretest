package ru.crystals.test2.operDay.tableReports;


import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.test2.basic.AbstractPage;
import static ru.crystals.test2.utils.FlexMediator.*;


public class  GoodsOnTKConfigPage extends AbstractPage{
	
	static final String ID_OPERDAYSWF = "OperDay";
	static final String LOCATOR_SETERPCODE = "goodSearchWidget";
	static final String LOCATOR_SETSHOP= "id:shopSearchWidget/id:searchTextBox";
	static final String LOCATOR_SETGOOD= "id:goodSearchWidget/id:searchTextBox";
	public static final String HTMLREPORT = "download_html";
	public static final String PDFREPORT = "download_pdf";
	public static final String EXCELREPORT = "download_excel";
	public static final String LOCATOR_ALERT_YES = "title=YES";
	public static final String LOCATOR_SUGGEST_CODE = "codeLabel";
	
	
	public GoodsOnTKConfigPage(WebDriver driver) {
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
	
	public void generateReport(String reportType){
		waitForElement(getDriver(), ID_OPERDAYSWF, LOCATOR_SETGOOD);
		// doFlexMouseDown чтобы убрать flexSuggest
		doFlexMouseDown(getDriver(), ID_OPERDAYSWF, reportType);
		clickElement(getDriver(), ID_OPERDAYSWF, reportType);
		
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
	
	public ArrayList<String> getHTMLReportResults(){
		ArrayList<String> result = new ArrayList<String>();
		getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table")));
		List<WebElement> tableValues = getDriver().findElements(By.xpath(".//span"));
		for (WebElement tableColl:tableValues) {
			result.add(tableColl.getText() );
		}
		return result;
	}
	
	
	
	
}
