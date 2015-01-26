package ru.crystals.set10.pages.sales.equipment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class EquipmentPage extends SalesPage{
	
	static final String BUTTON_NEW_EQUIPMENT = "label:Добавить оборудование";
	static final String LOCATOR_EQUIPMENT_ITEM = "className:DeviceRowRenderer/id:subContainer/className:UITextField;text:%s";
	static final String LOCATOR_TABLE = "id:templateTable/className:ListBaseContentHolder|1";
	
	public EquipmentPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public NewEquipmentPage addNewEquipment(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_NEW_EQUIPMENT);
		return new NewEquipmentPage(getDriver());
	}
	
	public int getEqupmentTypeCount(String equipmentItemName){
		waitForElementVisible(getDriver(), ID_SALESSWF, LOCATOR_TABLE);
		return getElementsNum(getDriver(), ID_SALESSWF, String.format(LOCATOR_EQUIPMENT_ITEM, equipmentItemName));
	}
	
	
	
	
}
