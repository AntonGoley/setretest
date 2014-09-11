package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.doFlexProperty;
import static ru.crystals.set10.utils.FlexMediator.waitForElement;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.AbstractPage;


public class SalesPreferencesPage extends AbstractPage{
	
	static final String ID_SALESSWF = "Sales";
	static final String LOCATOR_TABBAR= "toggleBur";
	
	
	public SalesPreferencesPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	//TODO replace with factory
	public SalesGoodsTypesAndPaymentsTabPage  navigateTab(SalesPreferencesPageTabs salesPreferencesTab) {
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, new String[] {"selectedIndex", String.valueOf(salesPreferencesTab.ordinal()) });
		return new SalesGoodsTypesAndPaymentsTabPage(getDriver());
	}

	public enum SalesPreferencesPageTabs {
		COMMON_PREFERENCES,
		SECTIONS,
		GOODS_SHELVES,
		CHECKS_EXAMPLES,
		GOODS_TYPES_AND_PAYSMENTS
	}
	
}
