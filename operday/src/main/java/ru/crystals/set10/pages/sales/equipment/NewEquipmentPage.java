package ru.crystals.set10.pages.sales.equipment;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ru.crystals.set10.pages.basic.SalesPage;
import static ru.crystals.set10.utils.FlexMediator.*;

public class NewEquipmentPage extends SalesPage {
	
	static final String BUTTON_REGISTER_NEW_EQUIPMENT= "label=Зарегистрировать новое оборудование";
	static final String LOCATOR_EQUPMENT_GROUP_TABLE = "list1";
	static final String SELECT_EQUPMENT_GROUP = "id:list1/text:%s";
	static final String LOCATOR_EQUPMENT_ITEM_TABLE = "list2";
	
	public static final String TAB_CASHIES = "Кассы";
	public static final String TAB_SCALES = "Весы";
	
	
	public NewEquipmentPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	
	public EquipmentPage addEquipment(String equpmentGroup, String equpmentGroupIndex, String equpmentItem){
		//выделить и выбрать группу оборудования
		doFlexProperty(getDriver(), ID_SALESSWF,  LOCATOR_EQUPMENT_GROUP_TABLE, new String[]{"selectedIndex", equpmentGroupIndex});
		clickElement(getDriver(), ID_SALESSWF, String.format(SELECT_EQUPMENT_GROUP, equpmentGroup));
		
		//выделить и выбрать элемент оборудования
		doFlexProperty(getDriver(), ID_SALESSWF,  LOCATOR_EQUPMENT_ITEM_TABLE, new String[]{"selectedIndex", equpmentGroupIndex});
		
		clickElement(getDriver(), ID_SALESSWF, BUTTON_REGISTER_NEW_EQUIPMENT);
		return new EquipmentPage(getDriver());
	}
	
}
