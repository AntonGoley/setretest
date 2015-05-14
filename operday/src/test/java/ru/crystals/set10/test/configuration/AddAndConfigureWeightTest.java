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
import static ru.crystals.set10.pages.sales.shops.RetailShopInfoTabPage.*;
import static ru.crystals.set10.pages.sales.preferences.goodstypes.weight.WeightGoodPage.*;
import static ru.crystals.set10.pages.sales.preferences.SalesPreferencesPage.TAB_TYPES_GOODS_PAYMENTS;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODE_PREFIX;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_OFSET;
import static ru.crystals.set10.config.Config.WEIGHT_BARCODEGENERATION_PREFIX;;


public class AddAndConfigureWeightTest extends AbstractTest{
	
	MainPage mainPage;
	SalesPage salesPage;
	EquipmentPage equipmentPage;
	NewEquipmentPage  newEqupment;
	WeightGoodPage weightGood;
	RetailShopWeightTabPage shopWeightTab;
	
	String scalesGroup =  "VirtualScales";
	String scalesItem =  "VirtualScales"; 
	
	
	@BeforeClass
	public void doLogin(){
		mainPage = new LoginPage(getDriver(), Config.RETAIL_URL).doLogin(Config.MANAGER, Config.MANAGER_PASSWORD);
		salesPage = mainPage.openSales();		
	}
	
	@Test (description = "Добавление виртуальных весов (как оборудования) на магазин", 
			priority = 1)
	public void addVirtualScalesTest(){
		equipmentPage = salesPage.navigateMenu(1, EquipmentPage.class);
		
		newEqupment = equipmentPage.addNewEquipment();
		equipmentPage = newEqupment.addEquipment(scalesGroup, scalesItem);
		
		Assert.assertTrue(equipmentPage.getEqupmentTypeCount(scalesItem) > 0, 
				"Новые весы " + scalesItem + " не добавлены в обородувание");
		
	}
	
	@Test (description = "Привязка виртуальных весов к шаблону в магазине", 
			priority = 2)
	public void bindVirtualScalesTest(){
		
		int  totalScalesBefore;
		shopWeightTab = salesPage.navigateMenu(0, RetailShopInfoTabPage.class)
				.navigateTab(TAB_WEIGHT, RetailShopWeightTabPage.class);
		
		totalScalesBefore = shopWeightTab.getBindedWeightsCount();
		
		shopWeightTab.bindWeight("Фасовочные", "VirtualScales");
		
		
		Assert.assertTrue(shopWeightTab.getBindedWeightsCount() > totalScalesBefore, "Весы не добавлены в магазин");
	}
	
	@Test (description = "Насткройка генерации штрихкодов для весового товара (Типы товаров и оплат)", 
			priority = 3)
	public void bindBarCodeForWeightGoodTest(){
		weightGood = salesPage
				.navigateMenu(10, SalesPreferencesPage.class)
				.navigateTab(TAB_TYPES_GOODS_PAYMENTS)
				.selectProductTypeItem(ProductTypeItems.WEIGHT_GOOD, WeightGoodPage.class);
		weightGood
			.setPLUGeneration(PLU_GENERATION_ERP_AND_BAR_CODE)
			.setGoodAction(ACTION_FASOVKA)
			.setPrefix(WEIGHT_BARCODE_PREFIX)
			.addGoodAction();
			
		weightGood	
			.setGoodAction(ACTION_FASOVKA)
			.setPrefix(WEIGHT_BARCODEGENERATION_PREFIX)
			.setPLUOfset(WEIGHT_BARCODEGENERATION_OFSET)
			.addGoodAction()
			.goBack();
	}
	
	
}
