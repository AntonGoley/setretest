package ru.crystals.set10.pages.sales.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;
import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;


public class SalesPreferencesPage extends SalesPage{
	
	
	static final String LOCATOR_TABBAR= "id:firstScreen/id:toggleBur";
	public static final String TAB_TYPES_GOODS_PAYMENTS = "5";
	
	public SalesPreferencesPage(WebDriver driver) {
		super(driver);
	}
	
	//TODO replace with factory
	public SalesGoodsTypesAndPaymentsTabPage  navigateTab(String salesPreferencesTab) {
		/*
		 * Ждем, что загрузились все вкладки
		 */
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, new String[]{"numChildren","6"});
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, new String[]{"selectedIndex", salesPreferencesTab});
		return new SalesGoodsTypesAndPaymentsTabPage(getDriver());
	}
	
}
