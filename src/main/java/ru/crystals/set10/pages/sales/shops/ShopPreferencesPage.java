package ru.crystals.set10.pages.sales.shops;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.basic.BasicElements;
import static ru.crystals.set10.pages.basic.BasicElements.*;
import static ru.crystals.set10.utils.FlexMediator.*;


public class ShopPreferencesPage extends AbstractPage {
	
	
	static final String LOCATOR_SHOP_NUMBER_INPUT = "shopNumberTI";
	static final String LOCATOR_SHOP_NAME_INPUT = "shopNameTI";
	static final String LOCATOR_VIRTUAL_CHECKBOX = "virtualShopCB";
	static final String LOCATOR_BACK_BUTTON = "label=К магазинам";
	static final String LOCATOR_ADD_CASH_TO_SHOP_BUTTON = "addCashB";
	static final String LOCATOR_CASHES_TAB = "shopSettingsTabNav";
	static final String LOCATOR_CASHES_COUNT_INPUT = "amountPole";
	
	public ShopPreferencesPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));

	}
	
	public ShopPreferencesPage setShopNumber(String number){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NUMBER_INPUT, number);
		return this;
	}
	
	public ShopPreferencesPage setName(String name){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NAME_INPUT, name);
		return this;
	}
	
	public ShopPreferencesPage ifShopUseOwnServer(boolean virtualServer){
		checkBoxValue(getDriver(), ID_SALESSWF, LOCATOR_VIRTUAL_CHECKBOX, virtualServer);
		return this;
	}
	
	public ShopPreferencesPage addCashes(String cashCount){
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_CASHES_TAB, new String[] {"selectedIndex", "1"});
		typeText(getDriver(), ID_SALESSWF, LOCATOR_CASHES_COUNT_INPUT, cashCount);
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_CASH_TO_SHOP_BUTTON);
		// ждем пока не обновится счетчик касс
		//TODO : сделать универсальный способ ожидания: сколько касс было, сколько ожидать, после добавления
		waitForElement(getDriver(), ID_SALESSWF, "numberOfItems=1");
		return this;
	}
	
	
	public ShopPage goBack(){
		return BasicElements.goBack(getDriver(), ShopPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
	
	
}
