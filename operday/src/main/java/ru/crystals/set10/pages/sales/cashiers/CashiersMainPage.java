package ru.crystals.set10.pages.sales.cashiers;

import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class CashiersMainPage extends SalesPage{
	
	static final String LOCATOR_ADD_NEW_CASHIER = "label:Добавить нового кассира";
	
	public CashiersMainPage(WebDriver driver) {
		super(driver);
	}
	
	
	public CashierConfigPage addCashier(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_NEW_CASHIER);
		return new CashierConfigPage(getDriver());
	}
}
