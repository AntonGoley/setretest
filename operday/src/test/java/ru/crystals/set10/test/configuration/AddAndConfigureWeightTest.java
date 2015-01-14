 package ru.crystals.set10.test.configuration;


import org.testng.Assert;
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
		salesPage = mainPage.openSales();		
	}
	
	@Test (description = "Добавление виртуальных весов (как оборудования) на магазин", 
			priority = 1)
	public void addVirtualScalesTest(){
		//TODO: добавить проверку
		newEqupment = salesPage.navigateMenu(SALES_MENU_EQUIPMENT, "1", EquipmentPage.class)
			.addNewEquipment();
		newEqupment.addEquipment("VirtualScales", "4", "VirtualScales")
			.ifEqupmentOnPage("VirtualScales");
	}
	
	@Test (description = "Привязка виртуальных весов к шаблону в магазине", 
			priority = 2)
	public void bindVirtualScalesTest(){
		int  totalScalesBefore;
		shopWeightTab = salesPage.navigateMenu(SALES_MENU_SHOP_PREFERENCES, "0", RetailShopInfoTabPage.class)
				.navigateTab(TAB_WEIGHT, RetailShopWeightTabPage.class);
		
		totalScalesBefore = shopWeightTab.getBindedWeightsCount();
		
		shopWeightTab.bindWeight("Фасовочные", "VirtualScales");
		shopWeightTab.ifWeightBinded("VirtualScales");
		Assert.assertTrue(shopWeightTab.getBindedWeightsCount() > totalScalesBefore, "Весы не добавлены в магазин");
	}
	
	@Test (enabled = false, description = "Насткройка генерации штрихкодов для весового товара (Типы товаров и оплат)", 
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
