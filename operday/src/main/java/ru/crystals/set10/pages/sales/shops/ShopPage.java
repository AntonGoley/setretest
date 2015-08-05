package ru.crystals.set10.pages.sales.shops;


import org.openqa.selenium.WebDriver;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class ShopPage extends SalesPage{
	
	static final String LOCATOR_ADD_SHOP_BUTTON = "id:addShopB";
	static final String LOCATOR_CITY_NAME_INPUT = "id:cityNameTI";
	static final String LOCATOR_SHOPS_DATAGRID = "id:dataGrid";
	static final String LOCATOR_BUTTON_PREFERENCES = "id:editShopB";
	static final String LOCATOR_SHOP_FILTER = "filterField";
	
	
	public ShopPage(WebDriver driver) {
		super(driver);
	}
	
	public ShopPreferencesPage addShop(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_SHOP_BUTTON);
		return new ShopPreferencesPage(getDriver());
	}
	
	/*
	 * Вводим название магазина в фильтре и ждем, пока отобразятся результаты поиска
	 * Название магазина должно быть уникальным
	 */
	public ShopPreferencesPage openShopPreferences(String shopName){
		//ищем магазин
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_FILTER, shopName);
		DisinsectorTools.delay(1000);
		// ждем, пока количество найденных объектов = 1
		waitForElement(getDriver(), ID_SALESSWF, "realDataLength=1");
		// выбираем первый в списке (и единственный) объект
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_SHOPS_DATAGRID, new String[] {"selectedIndex", "0"});
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_BUTTON_PREFERENCES, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_BUTTON_PREFERENCES);
		return new ShopPreferencesPage(getDriver());
	}
	
	
}
