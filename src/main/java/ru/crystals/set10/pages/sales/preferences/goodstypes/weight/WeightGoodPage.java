package ru.crystals.set10.pages.sales.preferences.goodstypes.weight;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class WeightGoodPage extends SalesPage{
	
	
	static final String SELECT_GOOD_ACTION = "productActionList";
	static final String SELECT_PREFIX = "prefixesCombo";
	
	static final String BUTTON_ADD_GOOD_ACTION= "label=Добавить действие с товаром";
	
	public static final String PLU_GENERATION_ERP = "ERP";
	public static final String PLU_GENERATION_GOOD_CODE = "CodeEqual";
	public static final String PLU_GENERATION_BAR_CODE = "BarCodeEqual";

	public static String ACTION_UCENKA = "Уценка";
	public static String ACTION_FASOVKA = "Фасовка";
	
	public WeightGoodPage(WebDriver driver) {
		super(driver);
		getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(ID_SALESSWF)));
	}
	
	public WeightGoodPage setGoodAction(String action){
		selectElement(getDriver(), ID_SALESSWF,  SELECT_GOOD_ACTION, action);
		return this;
	}
	
	public WeightGoodPage setPrefix(String prefix){
		selectElement(getDriver(), ID_SALESSWF,  SELECT_PREFIX, prefix);
		return this;
	}
	
	
	public WeightGoodPage setPLUGeneration(String PLUGeneration){
		checkBoxValue(getDriver(), ID_SALESSWF, PLUGeneration, true);
		return this;
	}
	
	public void addGoodAction(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_ADD_GOOD_ACTION);
		//TODO: дождаться появления в списке
		DisinsectorTools.delay(1000);
	}
	
}
