package ru.crystals.set10.pages.sales.equipment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewEquipmentPage extends SalesPage {
	
	static final String BUTTON_REGISTER_NEW_EQUIPMENT= "label:Зарегистрировать новое оборудование";
	static final String SELECT_EQUPMENT_GROUP = "id:list1/className:Text;text:%s";
	static final String LOCATOR_EQUPMENT_ITEM_TABLE = "id:list2/className:Text;text:%s|0";
	
	
	public static final String TAB_CASHIES = "Кассы";
	public static final String TAB_SCALES = "Весы";
	
	
	public NewEquipmentPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public EquipmentPage addEquipment(String equpmentGroup, String equpmentItem){
		//выделить и выбрать группу оборудования
		DisinsectorTools.delay(1000);
		doFlexMouseDown(getDriver(), ID_SALESSWF, String.format(SELECT_EQUPMENT_GROUP, equpmentGroup));
		clickElement(getDriver(), ID_SALESSWF, String.format(SELECT_EQUPMENT_GROUP, equpmentGroup));
		
		doFlexMouseDown(getDriver(), ID_SALESSWF, String.format(LOCATOR_EQUPMENT_ITEM_TABLE, equpmentItem));
		clickElement(getDriver(), ID_SALESSWF, String.format(LOCATOR_EQUPMENT_ITEM_TABLE, equpmentItem));
		
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER_NEW_EQUIPMENT);
		waitSpinner(ID_SALESSWF);
		return new EquipmentPage(getDriver());
	}
	
}
