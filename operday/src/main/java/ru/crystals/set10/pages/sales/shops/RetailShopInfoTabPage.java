package ru.crystals.set10.pages.sales.shops;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.SalesPage;

public class RetailShopInfoTabPage extends SalesPage{
	
	static final String TAB_LOCATOR = "id:mainVBox/id:toggleBur|0";
	
	public static final String TAB_INFORMATION = "0";
	public static final String TAB_CASHES = "1";
	public static final String TAB_PRINTERS = "2";
	public static final String TAB_WEIGHT = "3";
	
	public RetailShopInfoTabPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T navigateTab(String tabIndex, Class<T> tab){
		waitForProperty(getDriver(), ID_SALESSWF, TAB_LOCATOR, new String[]{"numChildren","4"});
		doFlexProperty(getDriver(), ID_SALESSWF, TAB_LOCATOR, new String[] {"selectedIndex", tabIndex});
		return PageFactory.initElements(getDriver(), tab);
	}
}
