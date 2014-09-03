package ru.crystals.set10.pages.sales.shops;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static ru.crystals.set10.utils.FlexMediator.*;

import ru.crystals.set10.pages.basic.SalesPage;


public class ShopPage extends SalesPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_ADD_SHOP_BUTTON = "addShopB";
	static final String LOCATOR_CITY_NAME_INPUT = "cityNameTI";
	static final String LOCATOR_SHOPS_DATAGRID = "dataGrid";
	static final String LOCATOR_BUTTON_PREFERENCES = "label=Настройки";
	
	
	public ShopPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public ShopPreferencesPage addShop(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_SHOP_BUTTON);
		return new ShopPreferencesPage(getDriver());
	}
	
	public ShopPreferencesPage openFirstShopPreferences(){
		// выбрать первый в списке магазин
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_SHOPS_DATAGRID, new String[] {"selectedIndex", "1"});
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_BUTTON_PREFERENCES);
		return new ShopPreferencesPage(getDriver());
	}
	
	
}
