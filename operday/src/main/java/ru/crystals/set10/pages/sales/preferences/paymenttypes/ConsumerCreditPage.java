package ru.crystals.set10.pages.sales.preferences.paymenttypes;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.sales.preferences.AbstractGoodAndPaymentPreferencesPage;

/*
 * Типы товаров и оплат: страница настроек "Подарочная карта"
 */
public class ConsumerCreditPage extends AbstractGoodAndPaymentPreferencesPage {
	
	public static final String CHECKBOX_ALLOW_RETURN= "Разрешить возврат";
	
	
	public ConsumerCreditPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
}
