package ru.crystals.set10.pages.sales.shops;

import org.openqa.selenium.WebDriver;
import ru.crystals.set10.pages.basic.SalesPage;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.utils.FlexMediator.*;


public class ShopPreferencesPage extends SalesPage {
	
	static final String LOCATOR_SHOP_NUMBER_INPUT = "id:shopNumberTI";
	static final String LOCATOR_SHOP_NAME_INPUT = "id:shopNameTI";
	static final String LOCATOR_VIRTUAL_CHECKBOX = "id:virtualShopCB";
	static final String LOCATOR_BACK_BUTTON = "label:К магазинам";
	//addCashB
	static final String LOCATOR_ADD_CASH_TO_SHOP_BUTTON = "id:addCashB";
	static final String LOCATOR_TABS = "id:shopSettingsTabNav/label:Кассы|0";
	static final String LOCATOR_CASHES_COUNT_INPUT = "id:amountPole";
	static final String LOCATOR_ADD_JURISTIC_PERSON_BUTTON = "id:addLegalEntityButton";
	static final String LOCATOR_CASHES_TOTAL = "id:shopCashesTab/id:pagination";

	
	public ShopPreferencesPage(WebDriver driver) {
		super(driver);
	}
	
	public ShopPreferencesPage setShopNumber(String number){
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NUMBER_INPUT, number);
		return this;
	}
	
	public ShopPreferencesPage setName(String name){
		log.info("Задать имя магазину: " + name);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_SHOP_NAME_INPUT, name);
		return this;
	}
	
	public ShopPreferencesPage ifShopUseOwnServer(boolean virtualServer){
		checkBoxValue(getDriver(), ID_SALESSWF, LOCATOR_VIRTUAL_CHECKBOX, virtualServer);
		return this;
	}
	
	public ShopPreferencesPage addCashes(int cashCount){
		int totalCashesBefore;
		//TODO: вынести Кассы в элемент
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_TABS);
		DisinsectorTools.delay(1500);
		typeText(getDriver(), ID_SALESSWF, LOCATOR_CASHES_COUNT_INPUT, String.valueOf(cashCount));
		totalCashesBefore = getTotalCashesInShop();
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_CASH_TO_SHOP_BUTTON);
		waitSpinner(ID_SALESSWF);
		/*
		 *  ждем пока счетчик касс увеличится на cashCount
		 */
		//waitForElement(getDriver(), ID_SALESSWF, String.format("numberOfItems=%s", String.valueOf(totalCashesBefore + cashCount)));
		waitForProperty(getDriver(), ID_SALESSWF, LOCATOR_CASHES_TOTAL, new String[]{"numberOfItems", String.valueOf(totalCashesBefore + cashCount)});
		log.info("Добавить кассы:" + cashCount );
		return this;
	}
	
	public int getTotalCashesInShop(){
		return Integer.valueOf(
				getElementProperty(getDriver(), ID_SALESSWF, LOCATOR_CASHES_TOTAL, "numberOfItems"));		 
	}
	
	public JuristicPersonPage addJuristicPerson(){
		log.info("Добавить юридический адрес");
		clickElement(getDriver(), ID_SALESSWF, LOCATOR_ADD_JURISTIC_PERSON_BUTTON);
		return new JuristicPersonPage(getDriver());
	}
	
	public ShopPage goBack(){
		return goBack(getDriver(), ShopPage.class, ID_SALESSWF, LOCATOR_BACK_BUTTON);
	}
	
	
}
