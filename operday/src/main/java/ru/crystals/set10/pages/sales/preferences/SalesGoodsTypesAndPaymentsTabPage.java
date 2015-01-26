package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;


public class SalesGoodsTypesAndPaymentsTabPage extends SalesPage{
	
	static final String LOCATOR_PRODUCT_TYPE_LIST = "id:productTypeList";
	static final String LOCATOR_PRODUCT_PREFERENCES_BUTTON = "id:setSelectedProductTypeBtn";
	
	
	public SalesGoodsTypesAndPaymentsTabPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public <T> T selectProductTypeItem(ProductTypeItems productTypeItem, Class<T> page) {
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_TYPE_LIST, new String[] {"selectedIndex", String.valueOf(productTypeItem.ordinal()) });
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_PREFERENCES_BUTTON, new String[] {"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_PRODUCT_PREFERENCES_BUTTON);
		return PageFactory.initElements(getDriver(), page);
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
