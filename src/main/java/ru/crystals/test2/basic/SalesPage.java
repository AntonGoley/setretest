package ru.crystals.test2.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static ru.crystals.test2.utils.FlexMediator.*;


public class SalesPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_MENUITEM = "topList";
	
	static final String SALES_MENU_SHOPS = "text=Магазины";
	static final String SALES_MENU_TOPOLOGY = "text=Топология";
	static final String SALES_MENU_EQUIPMENT = "text=Оборудование";
	static final String SALES_MENU_EXTERNAL_SYSTEMS  = "text=Внешние системы";
	static final String SALES_MENU_USERS = "text=Пользователи";
	static final String SALES_MENU_CASHIERS = "text=Кассиры";
	static final String SALES_MENU_CASH_PATTERNS = "text=Шаблоны касс";
	static final String SALES_MENU_WEIGHTER_PATTERNS = "text=Шаблоны весов";
	static final String SALES_MENU_PRICE_LIST_PATTERNS = "text=Шаблоны ценников";
	static final String SALES_MENU_SALES_GROUPS = "text=Группы продаж";
	static final String SALES_MENU_MODULES_PREFERENCES = "text=Настройки модулей";
	static final String SALES_MENU_PREFERENCES= "text=Общие настройки";
	
	
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
	
	// TODOD: подумать, как реализовать меню для ритейла и центрума
	public enum SalesMenuItemsAdmin {
		SHOPS (SALES_MENU_SHOPS),
	//	TOPOLOGY (SALES_MENU_TOPOLOGY),
		EQUIPMENT (SALES_MENU_EQUIPMENT),
		EXTERNAL_SYSTEMS (SALES_MENU_EXTERNAL_SYSTEMS),
		USERS (SALES_MENU_USERS),
		CASHIERS (SALES_MENU_CASHIERS),
		CASH_PATTERNS (SALES_MENU_CASH_PATTERNS),
		WEIGHTER_PATTERNS (SALES_MENU_WEIGHTER_PATTERNS),
		PRICE_LIST_PATTERNS (SALES_MENU_PRICE_LIST_PATTERNS),
		SALES_GROUPS (SALES_MENU_SALES_GROUPS),
		MODULES_PREFERENCES (SALES_MENU_MODULES_PREFERENCES),
		FAKE ("FAKE"),
		PREFERENCES (SALES_MENU_PREFERENCES);
		
		String locator;
		SalesMenuItemsAdmin(String locator){
			this.locator = locator;
		}
		
		public String getLocator(){
			return this.locator;
		}
		
	}
}
