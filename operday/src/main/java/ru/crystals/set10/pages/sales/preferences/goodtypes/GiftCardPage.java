package ru.crystals.set10.pages.sales.preferences.goodtypes;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.sales.preferences.AbstractGoodAndPaymentPreferencesPage;

/*
 * Типы товаров и оплат: страница настроек "Подарочная карта"
 */
public class GiftCardPage extends AbstractGoodAndPaymentPreferencesPage {
	
	public static final String CHECKBOX_ALLOW_MANUAL_CARD_NUMBER_INPUT = "Разрешить ручной ввод номера карты";
	
	
	public GiftCardPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
}
