package ru.crystals.set10.pages.sales.externalsystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class ExternalSystemsPage extends SalesPage{
	
	static final String BUTTON_ADD = "id:addButton";
	static final String LOCATOR_ERP_GRID = "id:templateGrid";
	static final String LOCATOR_EXTERNAL_PROCESSINGS_TABLE = "id:processingTable";
	static final String LOCATOR_BANKS_TABLE = "id:bankTable";
	
	public static final String TAB_NAME_BANKS = "Банки";
	public static final String TAB_EXTERNAL_PROCESSINGS = "Внешние процессинги";
	public static final String TAB_NAME_ERP= "ERP";
	
	public ExternalSystemsPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public ExternalSystemsPage  navigateTab(String tabName){
		clickElement(getDriver(), ID_SALESSWF, String.format("className:Tab;label:%s", tabName) );
		return this;
	}
	
	public  <T> T  addEntity(Class<T> page) {
		String buttonLocator = "label:Добавить банк";	

		if (page.equals(NewExternalProcessingPage.class)) {
			buttonLocator = "label:Добавить оператора";
		} 
		
		if (page.equals(NewERPPage.class)) {
			buttonLocator = "label:Добавить ERP";
		}
		
		clickElement(getDriver(), ID_SALESSWF, buttonLocator);
		return PageFactory.initElements(getDriver(), page);
	}
	
	
	private boolean ifNoElementsOnPage(String locator){
		if (getElementsNum(getDriver(), ID_SALESSWF, locator) == 0) {
			return true;
		};
		return false;
	}
	
	public int getERPsCount(){
		navigateTab(TAB_NAME_ERP);
		if (ifNoElementsOnPage(LOCATOR_ERP_GRID)) {
			return 0;
		};
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_ERP_GRID, "realDataLength"));
	}
	
	public int getExternalProcessingCount(){
		navigateTab(TAB_EXTERNAL_PROCESSINGS);
		if (ifNoElementsOnPage(LOCATOR_EXTERNAL_PROCESSINGS_TABLE)) {
			return 0;
		};
		/* возвращаемое значение содержит заголовки: т.е если добавляем новую группу в таблице добавится +2 элемента*/
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_EXTERNAL_PROCESSINGS_TABLE, "length")); 
	}
	
	public int getBanksCount(){
		navigateTab(TAB_NAME_BANKS);
		if (ifNoElementsOnPage(LOCATOR_BANKS_TABLE)) {
			return 0;
		};
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_BANKS_TABLE, "length"));
	}
}
