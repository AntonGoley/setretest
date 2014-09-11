package ru.crystals.set10.pages.sales.shops;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.test2.basic.AbstractPage;
import ru.crystals.test2.basic.BasicElements;
import static ru.crystals.test2.basic.BasicElements.*;
import static ru.crystals.test2.utils.FlexMediator.*;


public class ShopPreferencesPage extends AbstractPage {
	
	
	static final String LOCATOR_SHOP_NUMBER_INPUT = "shopNumberTI";
	static final String LOCATOR_SHOP_NAME_INPUT = "shopNameTI";
	static final String LOCATOR_VIRTUAL_CHECKBOX = "virtualShopCB";
	static final String LOCATOR_BACK_BUTTON= "label=К магазинам";
	
	
	public ShopPreferencesPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));

	}
	
	
	public ShopPreferencesPage setShopNumber(String number){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NUMBER_INPUT);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NUMBER_INPUT, number);
		return this;
	}
	
	public ShopPreferencesPage setName(String name){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NAME_INPUT);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NAME_INPUT, name);
		return this;
	}
	
	public ShopPreferencesPage ifShopUseOwnServer(boolean virtualServer){
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_VIRTUAL_CHECKBOX);
		checkBoxValue(getDriver(), ID_SALESSWF, LOCATOR_VIRTUAL_CHECKBOX, virtualServer);
		return this;
	}
	
	
	public ShopPage goBack(){
		return BasicElements.goBack(getDriver(), ShopPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
	
	
}
