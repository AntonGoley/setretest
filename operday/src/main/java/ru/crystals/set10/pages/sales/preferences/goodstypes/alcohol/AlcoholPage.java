package ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.sales.preferences.AbstractGoodAndPaymentPreferencesPage;

/*
 * Типы товаров и оплат: Крепкий алкоголь
 */
public class AlcoholPage extends AbstractGoodAndPaymentPreferencesPage {
	
	static final String LOCATOR_TABRESTRICTIONS= "alcoholExtTabNavigator";
	
	public AlcoholPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	//TODO: make factory
	public AlcoholTabsRestrictionsPage selectAlcoholTab(AlcoholTabs alcoholTab) {
		doFlexProperty(getDriver(), ID_SALESSWF, LOCATOR_TABRESTRICTIONS, new String[] {"selectedIndex", String.valueOf(alcoholTab.ordinal()) });
		return new AlcoholTabsRestrictionsPage(getDriver());
	}
	
	
	public enum AlcoholTabs{
		ALCOHOL_PREFERENCES,
		ALCOHOL_RESTRICTIONS
	}
	
}
