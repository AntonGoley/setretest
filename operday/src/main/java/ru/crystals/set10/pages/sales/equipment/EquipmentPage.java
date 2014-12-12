package ru.crystals.set10.pages.sales.equipment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class EquipmentPage extends SalesPage{
	
	static final String BUTTON_NEW_EQUIPMENT = "label=Добавить оборудование";
	static final String LOCATOR_EQUIPMENT_ITEM = "className:DeviceRowRenderer/text:%s";
	
	public EquipmentPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public NewEquipmentPage addNewEquipment(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_NEW_EQUIPMENT);
		return new NewEquipmentPage(getDriver());
	}
	
	public boolean ifEqupmentOnPage(String equipmentItemName){
		return waitForElementVisible(getDriver(), ID_SALESSWF, String.format(LOCATOR_EQUIPMENT_ITEM, equipmentItemName));
	}
}
