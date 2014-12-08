package ru.crystals.set10.pages.operday;


import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;


public class  HTMLRepotResultPage extends AbstractPage{
	
	ArrayList<String> reportResults = new ArrayList<String>();
	JavascriptExecutor js = (JavascriptExecutor) getDriver(); 
	
	public HTMLRepotResultPage(WebDriver driver) {
		super(driver);
		setReportResults();
	}
	
	private void setReportResults(){
		getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table")));
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
			//reportResults.remove(value);
		}
		return result;
	}
	
	public void removeValue(String value){
		reportResults.remove(value);
	}
	
	// Возвращает значение последней строки отчета, отчет должен быть открыт (visible)
	public String getLastLineColumnValue(int columnNumber){
		String cellValue = "";
		// если внутри тега td не содержится span, значит ячейка таблицы пустая
		long childNodesCount = (long) js.executeScript(String.format(
				"return document.evaluate(\"count(//table//tr[last()]/td[%s]/*)\", document, null, XPathResult.ANY_TYPE, null).numberValue; ", columnNumber));

		if (childNodesCount == 1) {
			cellValue =  getDriver().findElement(By.xpath(String.format("//table//tr[last()]/td[%s]/span", columnNumber))).getText();
		};	

		return cellValue;
	}
}
