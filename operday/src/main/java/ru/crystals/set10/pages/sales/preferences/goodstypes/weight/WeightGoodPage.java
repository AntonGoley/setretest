package ru.crystals.set10.pages.sales.preferences.goodstypes.weight;

import static ru.crystals.set10.utils.FlexMediator.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;


public class WeightGoodPage extends SalesPage{
	
	
	static final String SELECT_GOOD_ACTION = "id:productActionsComboBox";
	static final String SELECT_PREFIX = "id:prefixesComboBox";
	static final String LOCATOR_PLU_OFSET = "id:pluOffsetTextInput";
	
	static final String BUTTON_ADD_GOOD_ACTION= "label:Добавить действие с товаром";
	
	public static final String PLU_GENERATION_ERP = "id:erpGenerationRadioButton";
	public static final String PLU_GENERATION_GOOD_CODE = "id:codeGenerationRadioButton";
	public static final String PLU_GENERATION_BAR_CODE = "id:barCodeGenerationRadioButton";
	public static final String PLU_GENERATION_ERP_AND_BAR_CODE = "id:erpAndBarCodeGenerationRadioButton";
	
	
	public static final String BACK_BUTTON = "label:К типам товаров и оплат";

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
		log.info("Префикс для генерации баркода весовоготовара: " + prefix);
		return this;
	}
	
	public WeightGoodPage setPLUOfset(String ofset){
		typeText(getDriver(), ID_SALESSWF,  LOCATOR_PLU_OFSET, ofset);
		log.info("Смещение PLU: " + ofset);
		return this;
	}
	
	public WeightGoodPage setPLUGeneration(String PLUGeneration){
		radioButtonValue(getDriver(), ID_SALESSWF, PLUGeneration, true);
		log.info("Способ генерации PLU: " + PLUGeneration);
		return this;
	}
	
	public WeightGoodPage addGoodAction(){
		clickElement(getDriver(), ID_SALESSWF, BUTTON_ADD_GOOD_ACTION);
		//TODO: дождаться появления в списке
		DisinsectorTools.delay(1000);
		return this;
	}
	
	public SalesPage goBack(){
		clickElement(getDriver(), ID_SALESSWF, BACK_BUTTON);
		DisinsectorTools.delay(1000);
		return new SalesPage(getDriver());
	}
	
}
