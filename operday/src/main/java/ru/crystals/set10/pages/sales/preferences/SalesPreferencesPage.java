package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;


public class SalesPreferencesPage extends SalesPage{
	
	
	static final String LOCATOR_TABBAR= "toggleBur";
	public static final String TAB_TYPES_GOODS_PAYMENTS = "Типы товаров и оплат";
	
	
	public SalesPreferencesPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	//TODO replace with factory
	public SalesGoodsTypesAndPaymentsTabPage  navigateTab(String salesPreferencesTab) {
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, TAB_TYPES_GOODS_PAYMENTS);
		return new SalesGoodsTypesAndPaymentsTabPage(getDriver());
	}
	
}
