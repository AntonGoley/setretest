package ru.crystals.set10.pages.sales.shops;


import org.openqa.selenium.WebDriver;
import static ru.crystals.set10.utils.FlexMediator.*;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;

public class RetailShopWeightTabPage extends SalesPage{
	
	static final String LOCATOR_WEIGHT_PATTERN = "id:groupSalesSelector";
	static final String LOCATOR_WEIGHT_MODEL = "id:displaySelector";
	static final String BUTTON_ADD_TO_SHOP = "id:addScalesButton";
	static final String TABLE_WEIGHT_ITEMS = "id:scalesListFlexTable";
	
	static final String TAB_LOCATOR = "tabBar";
	
	public RetailShopWeightTabPage(WebDriver driver) {
		super(driver);
	}
	
	public void bindWeight(String weightPattern, String weightModel){
		int weightBefore = getBindedWeightsCount();
		long timeout = 100;
		selectElement(getDriver(), ID_SALESSWF, LOCATOR_WEIGHT_PATTERN, weightPattern);
		selectElement(getDriver(), ID_SALESSWF, LOCATOR_WEIGHT_MODEL, weightModel);
		clickElement(getDriver(), ID_SALESSWF, BUTTON_ADD_TO_SHOP);
		/*
		 * Ждем, пока весы не добавятся в таблицу
		 */
		while (getBindedWeightsCount() <= weightBefore) {
			DisinsectorTools.delay(timeout);
			timeout += timeout;
			if (timeout > 10000) {
				log.info("Весы не добавлены в магазин!!!");
				break;
			}
		}
		
	}
	
	public int getBindedWeightsCount(){
		String result;
		result = getElementProperty(getDriver(), ID_SALESSWF, TABLE_WEIGHT_ITEMS, "length");
		log.info("Всего привязано весов: " + result);
		return Integer.valueOf(result);
	}
	
//	public boolean ifWeightBinded(String weightModel){
//		return waitForElementVisible(getDriver(), ID_SALESSWF, String.format(LOCATOR_WEIGHT_ITEM, weightModel));
//	}
	
	
}
