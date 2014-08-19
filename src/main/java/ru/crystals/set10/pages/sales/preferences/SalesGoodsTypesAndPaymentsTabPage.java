package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol.AlcoholPage;


public class SalesGoodsTypesAndPaymentsTabPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_PRODUCT_TYPE_LIST = "productTypeList";
	static final String LOCATOR_PRODUCT_PREFERENCES_BUTTON = "label=Настройки";
	
	
	public SalesGoodsTypesAndPaymentsTabPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	// TODO: rewrite to Abstract factory
	public AlcoholPage selectProductTypeItem(ProductTypeItems productTypeItem) {
		waitForElement(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_TYPE_LIST);
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_TYPE_LIST, new String[] {"selectedIndex", String.valueOf(productTypeItem.ordinal()) });
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_PREFERENCES_BUTTON, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_PREFERENCES_BUTTON);
		return new AlcoholPage(getDriver());
	}
	
	public enum ProductTypeItems {
		SINGLE_GOOD,
		ALCOHOL,
		WEIGHT_GOOD,
		SINGLE_WEIGHT_GOOD,
		GIFT_CARD
		// populate list
	}
	
}
