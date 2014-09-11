package ru.crystals.test2.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static ru.crystals.test2.utils.FlexMediator.*;


public class SalesPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_MENUITEM = "topList";
	 
	
	public SalesPage(WebDriver driver) {
		super(driver);
		//switchWindow(true);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	  
	public <T> T navigateMenu(SalesMenuItemsAdmin menuItem, Class<T> page){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_MENUITEM);
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_MENUITEM, new String[] {"selectedIndex", String.valueOf(menuItem.ordinal()) });
		clickElement(getDriver(), ID_SALESSWF, menuItem.getLocator());
		return PageFactory.initElements(getDriver(), page);
	}
	
	public enum SalesMenuItemsAdmin {
		SHOPS ("text=Магазины"),
		TOPOLOGY ("text=Топология"),
		EQUIPMENT ("text=Оборудование"),
		EXTERNAL_SYSTEMS ("text=Внешние системы"),
		USERS ("text=Пользователи"),
		CASHIERS ("text=Кассиры"),
		CASH_PATTERNS ("text=Шаблоны касс"),
		WEIGHTER_PATTERNS ("text=Шаблоны весов"),
		PRICE_LIST_PATTERNS ("text=Шаблоны ценников"),
		SALES_GROUPS ("text=Группы продаж"),
		MODULES_PREFERENCES ("text=Настройки модулей"),
		PREFERENCES ("text=Общие настройки");
		
		String locator;
		SalesMenuItemsAdmin(String locator){
			this.locator = locator;
		}
		
		public String getLocator(){
			return this.locator;
		}
		
	}
	
}
