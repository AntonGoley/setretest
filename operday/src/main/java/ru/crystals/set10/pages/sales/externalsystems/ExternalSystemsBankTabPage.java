package ru.crystals.set10.pages.sales.externalsystems;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class ExternalSystemsBankTabPage extends SalesPage{
	
	static final String BUTTON_NEW_BANK = "label=Добавить банк";
	
	
	public ExternalSystemsBankTabPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public NewBankPage addNewBank(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_NEW_BANK);
		return new NewBankPage(getDriver());
	}
	
	public void setBankNameForProcessing(String bankName){
		
	}
	
//	public boolean ifEqupmentOnPage(String equipmentItemName){
//		return waitForElementVisible(getDriver(), ID_SALESSWF, String.format(LOCATOR_EQUIPMENT_ITEM, equipmentItemName));
//	}
}
