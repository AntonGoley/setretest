package ru.crystals.set10.pages.sales.preferences.goodstypes.alcohol;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.sales.preferences.AbstractGoodAndPaymentPreferencesPage;

/*
 * Типы товаров и оплат: Крепкий алкоголь
 */
public class AlcoholPage extends AbstractGoodAndPaymentPreferencesPage {
	
	static final String LOCATOR_TAB = "id:alcoholExtTabNavigator/className:Tab;label:%s";
	
	public static final String ALCOHOL_PREFERENCES = "Настройки";
	public static final String ALCOHOL_RESTRICTIONS= "Ограничения";
	

	public AlcoholPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	/*
	 * Выбрать вкладку
	 */
	public <T> T selectAlcoholTab(String tabName, Class<T> tab) {
		doFlexMouseDown(getDriver(), ID_SALESSWF, String.format(LOCATOR_TAB, tabName));
		//doFlexProperty(getDriver(), ID_SALESSWF, String.format(LOCATOR_TAB, tabName));
		return PageFactory.initElements(getDriver(), tab);
	}
	
	
}
