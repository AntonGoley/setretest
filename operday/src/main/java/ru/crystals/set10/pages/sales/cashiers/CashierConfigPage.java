package ru.crystals.set10.pages.sales.cashiers;

import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class CashierConfigPage extends SalesPage{
	
	static final String LOCATOR_LASTNAME = "id:lastName";
	static final String LOCATOR_FIRSTNAME = "id:firstName";
	static final String LOCATOR_MIDDLENAME = "id:middleName";
	static final String LOCATOR_TABNUMBER = "id:table_number";
	static final String LOCATOR_PASSWORD = "id:userPass";
	static final String LOCATOR_ROLE = "id:roleSelector";
	static final String LOCATOR_SHOP_NUMBER_ALL = "id:allShopsCheckBox";
	
	static final String LOCATOR_BACK_TO_CASHIERS_LIST= "label:К списку кассиров|1";
	
	public CashierConfigPage(WebDriver driver) {
		super(driver);
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
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_BACK_TO_CASHIERS_LIST, new String[]{"enabled", "true"});
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_BACK_TO_CASHIERS_LIST);
		DisinsectorTools.delay(1000);
		log.info("Добавить нового кассира");
		return new CashiersMainPage(getDriver());
	}
}
