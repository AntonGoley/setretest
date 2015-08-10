package ru.crystals.set10.pages.sales.module.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import ru.crystals.set10.pages.basic.SalesPage;


public class SalesModulesPreferencesPage extends SalesPage{
	
	public static final String TAB_MAINCASH = "2";
	public static final String LOCATOR_TABBAR = "id:tabNavigator/className:TabBar";
	
	
	public SalesModulesPreferencesPage(WebDriver driver) {
		super(driver);
	}
	
	public <T> T openTab(Class<T> tabPage, String tab){
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, new String[]{"numChildren","5"});
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_TABBAR, new String[]{"selectedIndex", tab});
		return PageFactory.initElements(getDriver(), tabPage);
	}
	
}
