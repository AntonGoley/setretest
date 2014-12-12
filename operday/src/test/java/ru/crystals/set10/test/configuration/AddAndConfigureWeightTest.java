 package ru.crystals.set10.test.configuration;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import ru.crystals.set10.config.Config;
import ru.crystals.set10.pages.basic.*;
import ru.crystals.set10.pages.sales.equipment.EquipmentPage;
import ru.crystals.set10.pages.sales.equipment.NewEquipmentPage;
import ru.crystals.set10.pages.sales.preferences.SalesGoodsTypesAndPaymentsTabPage.ProductTypeItems;
import ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage;
import ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage;
import ru.crystals.set10.pages.sales.shops.RetailShopInfoTabPage;
import ru.crystals.set10.pages.sales.shops.RetailShopWeightTabPage;
import ru.crystals.set10.test.AbstractTest;
import ru.crystals.set10.utils.DisinsectorTools;
import static ru.crystals.set10.pages.basic.SalesPage.*;
import static ru.crystals.set10.pages.sales.shops.RetailShopInfoTabPage.*;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage.*;
import static ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.*;

public class AddAndConfigureWeightTest extends AbstractTest{
	
	MainPage mainPage;
	SalesPage salesPage;
	NewEquipmentPage  newEqupment;
	WeightGoodPage weightGood;
	RetailShopWeightTabPage shopWeightTab;
	
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		// таймаут для певрого запуска
		DisinsectorTools.delay(3000);
		salesPage = mainPage.openSales();		
	}
	
	@Test (description = "Добавление весов EasyCom (как оборудования) на магазин", 
			priority = 1)
	public void addEasyComTest(){
		newEqupment = salesPage.navigateMenu(SALES_MENU_EQUIPMENT, "1", EquipmentPage.class)
			.addNewEquipment();
		newEqupment.addEquipment("EasyCom", "3", "EasyCom")
			.ifEqupmentOnPage("EasyCom");
	}
	
	@Test (description = "Привязка весов EasyCom к шаблону в магазине", 
			priority = 2)
	public void bindEasyComTest(){
		shopWeightTab = salesPage.navigateMenu(SALES_MENU_SHOP_PREFERENCES, "0", RetailShopInfoTabPage.class)
				.navigateTab(TAB_WEIGHT, RetailShopWeightTabPage.class);
		shopWeightTab.bindWeight("Прилавочные", "EasyCom");
		shopWeightTab.ifWeightBinded("EasyCom");
	}
	
	@Test (description = "Насткройка генерации штрихкодов для весового товара (Типы товаров и оплат)", 
			priority = 3)
	public void bindBarCodeForWeightGoodTest(){
		weightGood = salesPage
				.navigateMenu(SALES_MENU_PREFERENCES, "10", SalesPreferencesPage.class)
				.navigateTab(TAB_TYPES_GOODS_PAYMENTS)
				.selectProductTypeItem(ProductTypeItems.WEIGHT_GOOD, WeightGoodPage.class);
		weightGood
			.setGoodAction(ACTION_FASOVKA)
			.setPrefix("29")
			.setPLUGeneration(PLU_GENERATION_ERP)
			.addGoodAction();
	}
	
	
}
