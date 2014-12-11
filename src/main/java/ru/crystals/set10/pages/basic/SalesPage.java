package ru.crystals.set10.pages.basic;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class SalesPage extends AbstractPage{
	
	protected static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_MENUITEM = "topList";
	
	public static final String SALES_MENU_SHOPS = "text=Магазины";
	public static final String SALES_MENU_SHOP_PREFERENCES= "text=Настройки магазина";
	public static final String SALES_MENU_TOPOLOGY = "text=Топология";
	public static final String SALES_MENU_EQUIPMENT = "text=Оборудование";
	public static final String SALES_MENU_EXTERNAL_SYSTEMS  = "text=Внешние системы";
	public static final String SALES_MENU_USERS = "text=Пользователи";
	public static final String SALES_MENU_CASHIERS = "text=Кассиры";
	public static final String SALES_MENU_CASH_PATTERNS = "text=Шаблоны касс";
	public static final String SALES_MENU_WEIGHTER_PATTERNS = "text=Шаблоны весов";
	public static final String SALES_MENU_PRICE_LIST_PATTERNS = "text=Шаблоны ценников";
	public static final String SALES_MENU_SALES_GROUPS = "text=Группы продаж";
	public static final String SALES_MENU_MODULES_PREFERENCES = "text=Настройки модулей";
	public static final String SALES_MENU_PREFERENCES= "text=Общие настройки";
	
	
	public SalesPage(WebDriver driver) {
		super(driver);
		//switchWindow(true);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public <T> T navigateMenu(String menuItem, String menuItemPosition, Class<T> page){
		//TODO: убрать, когда будет нормальный выбор из меню
		DisinsectorTools.delay(1000);
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_MENUITEM, new String[] {"selectedIndex", menuItemPosition});
		clickElement(getDriver(), ID_SALESSWF, menuItem);
		return PageFactory.initElements(getDriver(), page);
	}
	
}
