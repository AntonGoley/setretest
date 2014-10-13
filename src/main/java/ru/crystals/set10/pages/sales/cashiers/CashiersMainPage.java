package ru.crystals.set10.pages.sales.cashiers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class CashiersMainPage extends SalesPage{
	
	static final String LOCATOR_ADD_NEW_CASHIER = "label=Добавить нового кассира";
	
	public CashiersMainPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public CashierConfigPage addCashier(){
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_NEW_CASHIER);
		return new CashierConfigPage(getDriver());
	}
}
