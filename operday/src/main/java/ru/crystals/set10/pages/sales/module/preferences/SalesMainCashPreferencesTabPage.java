package ru.crystals.set10.pages.sales.module.preferences;

import static ru.crystals.set10.utils.FlexMediator.*;

import java.math.BigDecimal;

import org.openqa.selenium.WebDriver;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.pages.basic.WarningPopUpMessage;
import ru.crystals.set10.utils.DisinsectorTools;


public class SalesMainCashPreferencesTabPage extends SalesPage{
	
	
	private static final String INPUT_BALANCE = "id:numberInput";
	private static final String INPUT_SHOP_PIN = "id:shopPINInput";
	private static final String CHECKBOX_ENABLE = "id:activateCheckBox";
	
	
	public SalesMainCashPreferencesTabPage(WebDriver driver) {
		super(driver);
	}
	
	public SalesMainCashPreferencesTabPage setDate(){
		return this;
	}
	
	public SalesMainCashPreferencesTabPage setInitialBalance(BigDecimal value){
		typeText(getDriver(), "application", INPUT_BALANCE, value.toPlainString().replace(".", ","));
		// TODO: залипуха, пока нет flexFocusOut 
		typeText(getDriver(), "application", INPUT_SHOP_PIN, "1");
		return this;
	}
	
	public SalesMainCashPreferencesTabPage turnMainCash(Boolean state){
		clickElement(getDriver(), "application", CHECKBOX_ENABLE);
		DisinsectorTools.delay(1000);
		new WarningPopUpMessage(getDriver()).makeDecision(WarningPopUpMessage.BUTTON_YES);
		return this;
	}
	
	public Boolean ifMainCashTurned(String balanceValue){
		//TODO:добавть проверку недоступности для редактирования даты ОД
		Boolean balanceEditStatus = waitForProperty(getDriver(), "application", INPUT_BALANCE, new String[]{"enabled", "false"});
		String balanceActualValue = getElementProperty(getDriver(), "application", INPUT_BALANCE, "text");
		
		if (balanceEditStatus && balanceActualValue.equals(balanceValue.replace(".", ","))){
			return true;
		} else  {
			return false;
		}

	}
}
