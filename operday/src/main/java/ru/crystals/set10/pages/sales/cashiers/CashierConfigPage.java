package ru.crystals.set10.pages.sales.cashiers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class CashierConfigPage extends SalesPage{
	
	static final String LOCATOR_LASTNAME = "lastName";
	static final String LOCATOR_FIRSTNAME = "firstName";
	static final String LOCATOR_MIDDLENAME = "middleName";
	static final String LOCATOR_TABNUMBER = "table_number";
	static final String LOCATOR_PASSWORD = "userPass";
	static final String LOCATOR_ROLE = "roleSelector";
	static final String LOCATOR_SHOP_NUMBER_ALL = "allShopsCheckBox";
	
	static final String LOCATOR_BACK_TO_CASHIERS_LIST= "label=К списку кассиров";
	
	public CashierConfigPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public CashiersMainPage addNewCashier(
			String name,
			String lastName,
			String middleName,
			String tabNumber,
			String password,
			String role){
		
		typeText(getDriver(), ID_SALESSWF, LOCATOR_LASTNAME, lastName);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_FIRSTNAME, name);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_MIDDLENAME, middleName);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_TABNUMBER, tabNumber);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_PASSWORD, password);
		selectElement(getDriver(), ID_SALESSWF, LOCATOR_ROLE, role);
		checkBoxValue(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NUMBER_ALL, true);
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_BACK_TO_CASHIERS_LIST);
		DisinsectorTools.delay(1000);
		log.info("Добавить нового кассира");
		return new CashiersMainPage(getDriver());
	}
}
