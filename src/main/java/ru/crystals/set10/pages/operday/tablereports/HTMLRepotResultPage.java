package ru.crystals.set10.pages.operday.tablereports;


import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;


public class  HTMLRepotResultPage extends AbstractPage{
	
	ArrayList<String> reportResults = new ArrayList<String>();
	
	public HTMLRepotResultPage(WebDriver driver) {
		super(driver);
		setReportResults();
	}
	
	private void setReportResults(){
		getWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//table")));
		List<WebElement> tableValues = getDriver().findElements(By.xpath(".//span"));
		for (WebElement tableColl:tableValues) {
			this.reportResults.add(tableColl.getText() );
		}
	}
	
	public int getReportSize() {
		return reportResults.size();
	}
	
	public boolean containsValue(String value){
		boolean result = false;
		if (this.reportResults.contains(value)) {
			result = true;
			reportResults.remove(value);
		}
		return result;
	}
	
	
}
